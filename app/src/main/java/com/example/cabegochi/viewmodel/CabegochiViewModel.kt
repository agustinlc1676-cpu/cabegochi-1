package com.example.cabegochi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cabegochi.ai.AIProvider
import com.example.cabegochi.ai.AIRequest
import com.example.cabegochi.ai.AIResponse
import com.example.cabegochi.ai.GeminiProvider
import com.example.cabegochi.memory.CabegochiDatabase
import com.example.cabegochi.memory.MemoryRepository
import com.example.cabegochi.memory.MemoryImporter

import com.example.cabegochi.model.CabegochiCharacter
import com.example.cabegochi.model.ChatMessage
import com.example.cabegochi.model.CotorreoLevel
import com.example.cabegochi.model.CulturalMemoryCard
import com.example.cabegochi.model.MessageRole
import com.example.cabegochi.model.UserProfile
import com.example.cabegochi.network.NetworkMonitor
import com.example.cabegochi.network.OfflinePhrases
import com.example.cabegochi.speech.DeviceVoice
import com.example.cabegochi.speech.SpeechInputState
import com.example.cabegochi.speech.SpeechToTextManager
import com.example.cabegochi.speech.TextToSpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CabegochiViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MemoryRepository
    private val ttsManager: TextToSpeechManager
    private val sttManager: SpeechToTextManager
    private val networkMonitor: NetworkMonitor
    private val aiProvider: AIProvider

    val userProfile: StateFlow<UserProfile>
    val culturalMemories: StateFlow<List<CulturalMemoryCard>>
    val chatMessages: StateFlow<List<ChatMessage>>

    val isOnline: StateFlow<Boolean>
    val isSpeaking: StateFlow<Boolean>
    val speechInputState: StateFlow<SpeechInputState>
    val availableVoices: StateFlow<List<DeviceVoice>>

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentOfflineQuote = MutableStateFlow(OfflinePhrases.getRandomPhrase())
    val currentOfflineQuote: StateFlow<String> = _currentOfflineQuote.asStateFlow()

    private val _activePlayingMessageId = MutableStateFlow<Long?>(null)
    val activePlayingMessageId: StateFlow<Long?> = _activePlayingMessageId.asStateFlow()

    init {
        val db = CabegochiDatabase.getDatabase(application)
        repository = MemoryRepository(db.cabegochiDao())
        ttsManager = TextToSpeechManager(application)
        sttManager = SpeechToTextManager(application)
        networkMonitor = NetworkMonitor(application)
        aiProvider = GeminiProvider()

        // Set account/device context: prefer stored device id or generate one
        val prefs = application.getSharedPreferences("cabegochi_prefs", 0)
        val deviceId = prefs.getString("device_id", null) ?: kotlin.run {
            val id = android.provider.Settings.Secure.getString(application.contentResolver, android.provider.Settings.Secure.ANDROID_ID) ?: java.util.UUID.randomUUID().toString()
            prefs.edit().putString("device_id", id).apply()
            id
        }
        val accountId = prefs.getString("account_id", "local") ?: "local"
        repository.setAccountContext(accountId, deviceId)

        // expose account actions
    }

    // Account flow wrappers
    fun createAccount(email: String?, phone: String?, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val id = repository.createAccount(email, phone)
                // persist accountId in prefs
                val prefs = getApplication<Application>().getSharedPreferences("cabegochi_prefs", 0)
                prefs.edit().putString("account_id", id).apply()
                repository.setAccountContext(id, prefs.getString("device_id", "local") ?: "local")
                onResult(id)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun requestOtp(accountId: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val otp = repository.requestOtp(accountId)
                onResult(otp)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }

    fun verifyOtp(accountId: String, otp: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val ok = repository.verifyOtp(accountId, otp)
                if (ok) {
                    val prefs = getApplication<Application>().getSharedPreferences("cabegochi_prefs", 0)
                    prefs.edit().putString("account_id", accountId).apply()
                    repository.setAccountContext(accountId, prefs.getString("device_id", "local") ?: "local")
                }
                onResult(ok)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
        // On first run (no interactions) optionally import the large memory asset to seed local cultural memory
        viewModelScope.launch {
            val profile = repository.getProfile()
            if (profile.interactionCount == 0) {
                try {
                    repository.importMemoryFromFile(application, application.filesDir.absolutePath + "/MEMORIA_TOTAL.txt")
                } catch (e: Exception) {
                    // fallback: import from assets if packaged
                    try {
                        MemoryImporter.importFromAssets(application, "MEMORIA_TOTAL.txt", db.cabegochiDao(), accountId, deviceId)
                    } catch (_: Exception) {
                        // ignore — importing is best-effort
                    }
                }
            }
        }

        userProfile = repository.userProfileFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserProfile()
        )

        culturalMemories = repository.culturalMemoriesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        chatMessages = repository.chatMessagesFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        isOnline = networkMonitor.isOnline
        isSpeaking = ttsManager.isSpeaking
        speechInputState = sttManager.inputState
        availableVoices = ttsManager.availableVoices

        // Observe network state to refresh character-specific offline quote whenever disconnected
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                if (!online) {
                    val character = userProfile.value.selectedCharacter
                    _currentOfflineQuote.value = OfflinePhrases.getRandomPhrase(character)
                }
            }
        }
    }

    fun sendMessage(userText: String) {
        val cleanText = userText.trim()
        if (cleanText.isBlank()) return

        viewModelScope.launch {
            // 1. Record user message
            repository.addMessage(MessageRole.USER, cleanText)

            // 2. Update memory patterns & interaction count
            repository.analyzeAndLearn(cleanText)

            // Check network
            val online = networkMonitor.isOnline.value
            if (!online) {
                val character = userProfile.value.selectedCharacter
                _currentOfflineQuote.value = OfflinePhrases.getRandomPhrase(character)
                return@launch
            }

            _isGenerating.value = true

            // Fetch context for AI
            val profile = repository.getProfile()
            val recentMessages = repository.getRecentMessages(8)
            val memories = repository.getCulturalMemories()

            val request = AIRequest(
                userMessage = cleanText,
                userProfile = profile,
                recentHistory = recentMessages,
                culturalMemories = memories
            )

            // Call AIProvider
            val response = aiProvider.generateReply(request)
            _isGenerating.value = false

            val replyText = when (response) {
                is AIResponse.Success -> response.text
                is AIResponse.QuotaExceeded -> response.message
                is AIResponse.Error -> response.fallbackText
            }

            // Save reply
            repository.addMessage(MessageRole.CABEGOCHI, replyText)

            // Auto-speak if enabled
            if (profile.autoSpeak) {
                ttsManager.speak(
                    text = replyText,
                    pitch = profile.speechPitch,
                    rate = profile.speechRate,
                    voiceName = profile.selectedVoiceName
                )
            }
        }
    }

    fun repeatVoice(text: String, messageId: Long? = null) {
        val profile = userProfile.value
        _activePlayingMessageId.value = messageId
        ttsManager.speak(
            text = text,
            pitch = profile.speechPitch,
            rate = profile.speechRate,
            voiceName = profile.selectedVoiceName
        )
    }

    fun stopVoice() {
        ttsManager.stop()
        _activePlayingMessageId.value = null
    }

    fun toggleAutoSpeak() {
        val current = userProfile.value
        val updated = current.copy(autoSpeak = !current.autoSpeak)
        viewModelScope.launch {
            repository.saveProfile(updated)
        }
    }

    fun updateCotorreoLevel(level: CotorreoLevel) {
        val current = userProfile.value
        val updated = current.copy(cotorreoLevel = level)
        viewModelScope.launch {
            repository.saveProfile(updated)
        }
    }

    fun updateCharacter(character: CabegochiCharacter) {
        val current = userProfile.value
        val updated = current.copy(selectedCharacter = character)
        viewModelScope.launch {
            repository.saveProfile(updated)
        }
    }

    fun updateVoiceSettings(voiceName: String?, pitch: Float, rate: Float) {
        val current = userProfile.value
        val updated = current.copy(
            selectedVoiceName = voiceName,
            speechPitch = pitch,
            speechRate = rate
        )
        viewModelScope.launch {
            repository.saveProfile(updated)
        }
    }

    fun testVoice(sampleText: String, pitch: Float, rate: Float, voiceName: String?) {
        ttsManager.speakTest(sampleText, pitch, rate, voiceName)
    }

    fun completeOnboarding(
        character: CabegochiCharacter,
        cabegochiName: String,
        userNickname: String,
        voiceName: String?,
        pitch: Float,
        rate: Float
    ) {
        viewModelScope.launch {
            val current = repository.getProfile()
            val cleanCabegochi = cabegochiName.trim().ifBlank { character.displayName }
            val cleanNick = userNickname.trim().ifBlank { "Papi" }

            val updated = current.copy(
                selectedCharacter = character,
                cabegochiName = cleanCabegochi,
                userNickname = cleanNick,
                selectedVoiceName = voiceName,
                speechPitch = pitch,
                speechRate = rate,
                firstLaunchCompleted = true
            )
            repository.saveProfile(updated)

            // Seed initial greeting message
            repository.addMessage(
                role = MessageRole.CABEGOCHI,
                text = character.sampleGreeting
            )

            // Auto speak greeting if enabled
            if (updated.autoSpeak) {
                ttsManager.speak(
                    text = character.sampleGreeting,
                    pitch = pitch,
                    rate = rate,
                    voiceName = voiceName
                )
            }
        }
    }

    fun startVoiceInput() {
        sttManager.startListening { transcribedText ->
            if (transcribedText.isNotBlank()) {
                sendMessage(transcribedText)
            }
        }
    }

    fun stopVoiceInput() {
        sttManager.stopListening()
    }

    fun refreshNetwork() {
        networkMonitor.forceCheck()
        val character = userProfile.value.selectedCharacter
        _currentOfflineQuote.value = OfflinePhrases.getRandomPhrase(character)
    }

    fun deleteCulturalMemory(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
            val profile = repository.getProfile()
            repository.addMessage(
                role = MessageRole.CABEGOCHI,
                text = "¡Borrón y cuenta nueva! Memoria caché reseteada. ¿De qué cotorreamos ahora?"
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
        sttManager.stopListening()
    }
}
