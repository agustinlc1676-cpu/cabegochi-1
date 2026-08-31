package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.cabegochi.ui.screens.ChatScreen
import com.example.cabegochi.ui.screens.OfflineScreen
import com.example.cabegochi.ui.screens.OnboardingScreen
import com.example.cabegochi.ui.screens.SettingsScreen
import com.example.cabegochi.ui.theme.CabegochiTheme
import com.example.cabegochi.viewmodel.CabegochiViewModel

enum class AppScreen {
    CHAT,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: CabegochiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CabegochiTheme {
                CabegochiApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CabegochiApp(viewModel: CabegochiViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val culturalMemories by viewModel.culturalMemories.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val speechInputState by viewModel.speechInputState.collectAsState()
    val availableVoices by viewModel.availableVoices.collectAsState()
    val currentOfflineQuote by viewModel.currentOfflineQuote.collectAsState()

    var currentScreen by remember { mutableStateOf(AppScreen.CHAT) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            // First time onboarding flow
            !userProfile.firstLaunchCompleted -> {
                OnboardingScreen(
                    availableVoices = availableVoices,
                    onComplete = { character, cabegochiName, userNickname, voiceName, pitch, rate ->
                        viewModel.completeOnboarding(
                            character = character,
                            cabegochiName = cabegochiName,
                            userNickname = userNickname,
                            voiceName = voiceName,
                            pitch = pitch,
                            rate = rate
                        )
                    },
                    onTestVoice = { sampleText, pitch, rate, voiceName ->
                        viewModel.testVoice(sampleText, pitch, rate, voiceName)
                    },
                    viewModel = viewModel
                )
            }

            // Offline Screen (Internet mandatory for live conversation as specified in prompt)
            !isOnline -> {
                OfflineScreen(
                    userProfile = userProfile,
                    offlineQuote = currentOfflineQuote,
                    onRetryConnection = { viewModel.refreshNetwork() }
                )
            }

            // Settings & Mi Cabegochi Screen
            currentScreen == AppScreen.SETTINGS -> {
                BackHandler {
                    currentScreen = AppScreen.CHAT
                }

                SettingsScreen(
                    userProfile = userProfile,
                    culturalMemories = culturalMemories,
                    availableVoices = availableVoices,
                    onBack = { currentScreen = AppScreen.CHAT },
                    onUpdateCharacter = { viewModel.updateCharacter(it) },
                    onUpdateCotorreoLevel = { viewModel.updateCotorreoLevel(it) },
                    onUpdateVoiceSettings = { voiceName, pitch, rate ->
                        viewModel.updateVoiceSettings(voiceName, pitch, rate)
                    },
                    onTestVoice = { sampleText, pitch, rate, voiceName ->
                        viewModel.testVoice(sampleText, pitch, rate, voiceName)
                    },
                    onToggleAutoSpeak = { viewModel.toggleAutoSpeak() },
                    onDeleteCulturalMemory = { viewModel.deleteCulturalMemory(it) },
                    onClearChatHistory = { viewModel.clearChatHistory() }
                )
            }

            // Main Chat Screen
            else -> {
                ChatScreen(
                    userProfile = userProfile,
                    chatMessages = chatMessages,
                    isOnline = isOnline,
                    isSpeaking = isSpeaking,
                    isGenerating = isGenerating,
                    speechInputState = speechInputState,
                    onSendMessage = { viewModel.sendMessage(it) },
                    onRepeatVoice = { text, id -> viewModel.repeatVoice(text, id) },
                    onStopVoice = { viewModel.stopVoice() },
                    onToggleAutoSpeak = { viewModel.toggleAutoSpeak() },
                    onStartVoiceInput = { viewModel.startVoiceInput() },
                    onStopVoiceInput = { viewModel.stopVoiceInput() },
                    onOpenSettings = { currentScreen = AppScreen.SETTINGS }
                )
            }
        }
    }
}

