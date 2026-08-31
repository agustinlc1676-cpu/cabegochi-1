package com.example.cabegochi.memory

import com.example.cabegochi.model.CabegochiCharacter
import com.example.cabegochi.model.ChatMessage
import com.example.cabegochi.model.CotorreoLevel
import com.example.cabegochi.model.CulturalMemoryCard
import com.example.cabegochi.model.MemoryCategory
import com.example.cabegochi.model.MessageRole
import com.example.cabegochi.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MemoryRepository(private val dao: CabegochiDao) {

    // Current account/device context (defaults to local single-device mode)
    private var currentAccountId: String = "local"
    private var currentDeviceId: String = "local"

    fun setAccountContext(accountId: String, deviceId: String) {
        currentAccountId = accountId
        currentDeviceId = deviceId
    }

    val userProfileFlow: Flow<UserProfile> = dao.getUserProfileFlow().map { entity ->
        if (entity == null) {
            UserProfile()
        } else {
            UserProfile(
                id = entity.id,
                userNickname = entity.userNickname,
                cabegochiName = entity.cabegochiName,
                selectedCharacter = CabegochiCharacter.fromId(entity.selectedCharacterId),
                selectedVoiceName = entity.selectedVoiceName,
                speechRate = entity.speechRate,
                speechPitch = entity.speechPitch,
                autoSpeak = entity.autoSpeak,
                cotorreoLevel = CotorreoLevel.fromLevel(entity.cotorreoLevelInt),
                interactionCount = entity.interactionCount,
                firstLaunchCompleted = entity.firstLaunchCompleted,
                createdAt = entity.createdAt,
                lastInteraction = entity.lastInteraction
            )
        }
    }

    val culturalMemoriesFlow: Flow<List<CulturalMemoryCard>> = dao.getAllCulturalMemoriesFlow().map { list ->
        list.map { entity ->
            CulturalMemoryCard(
                id = entity.id,
                category = try { MemoryCategory.valueOf(entity.category) } catch (e: Exception) { MemoryCategory.CALLBACK },
                key = entity.key,
                content = entity.content,
                timesUsed = entity.timesUsed,
                lastMentioned = entity.lastMentioned
            )
        }
    }

    val chatMessagesFlow: Flow<List<ChatMessage>> = dao.getAllMessagesFlow().map { list ->
        list.map { entity ->
            ChatMessage(
                id = entity.id,
                role = when (entity.role.uppercase()) {
                    "USER" -> MessageRole.USER
                    "CABEGOCHI" -> MessageRole.CABEGOCHI
                    else -> MessageRole.SYSTEM
                },
                text = entity.text,
                timestamp = entity.timestamp,
                characterMood = entity.characterMood
            )
        }
    }

    suspend fun getProfile(): UserProfile {
        val entity = dao.getUserProfile()
        return if (entity == null) {
            val default = UserProfile()
            saveProfile(default)
            default
        } else {
            UserProfile(
                id = entity.id,
                userNickname = entity.userNickname,
                cabegochiName = entity.cabegochiName,
                selectedCharacter = CabegochiCharacter.fromId(entity.selectedCharacterId),
                selectedVoiceName = entity.selectedVoiceName,
                speechRate = entity.speechRate,
                speechPitch = entity.speechPitch,
                autoSpeak = entity.autoSpeak,
                cotorreoLevel = CotorreoLevel.fromLevel(entity.cotorreoLevelInt),
                interactionCount = entity.interactionCount,
                firstLaunchCompleted = entity.firstLaunchCompleted,
                createdAt = entity.createdAt,
                lastInteraction = entity.lastInteraction
            )
        }
    }

    suspend fun saveProfile(profile: UserProfile) {
        val entity = UserProfileEntity(
            id = 1,
            accountId = currentAccountId,
            deviceId = currentDeviceId,
            userNickname = profile.userNickname,
            cabegochiName = profile.cabegochiName,
            selectedCharacterId = profile.selectedCharacter.id,
            selectedVoiceName = profile.selectedVoiceName,
            speechRate = profile.speechRate,
            speechPitch = profile.speechPitch,
            autoSpeak = profile.autoSpeak,
            cotorreoLevelInt = profile.cotorreoLevel.level,
            interactionCount = profile.interactionCount,
            firstLaunchCompleted = profile.firstLaunchCompleted,
            createdAt = profile.createdAt,
            lastInteraction = System.currentTimeMillis()
        )
        dao.insertOrUpdateProfile(entity)
    }

    suspend fun getRecentMessages(limit: Int = 10): List<ChatMessage> {
        // Prefer account/device-scoped recent messages when available
        val entities = try {
            dao.getRecentMessagesFor(currentAccountId, currentDeviceId, limit)
        } catch (e: Exception) {
            dao.getRecentMessages(limit)
        }
        return entities.reversed().map { entity ->
            ChatMessage(
                id = entity.id,
                role = when (entity.role.uppercase()) {
                    "USER" -> MessageRole.USER
                    "CABEGOCHI" -> MessageRole.CABEGOCHI
                    else -> MessageRole.SYSTEM
                },
                text = entity.text,
                timestamp = entity.timestamp,
                characterMood = entity.characterMood
            )
        }
    }

    suspend fun getCulturalMemories(): List<CulturalMemoryCard> {
        return dao.getAllCulturalMemories().map { entity ->
            CulturalMemoryCard(
                id = entity.id,
                category = try { MemoryCategory.valueOf(entity.category) } catch (e: Exception) { MemoryCategory.CALLBACK },
                key = entity.key,
                content = entity.content,
                timesUsed = entity.timesUsed,
                lastMentioned = entity.lastMentioned
            )
        }
    }

    suspend fun addMessage(role: MessageRole, text: String, mood: String? = null): Long {
        val entity = ChatMessageEntity(
            accountId = currentAccountId,
            deviceId = currentDeviceId,
            role = role.name,
            text = text,
            timestamp = System.currentTimeMillis(),
            characterMood = mood
        )
        dao.insertMessage(entity)
        return entity.id
    }

    suspend fun deleteMemory(id: Long) {
        dao.deleteCulturalMemory(id)
    }

    suspend fun clearAllHistory() {
        dao.clearChatHistory()
    }

    // Import large memory file into DB (delegates to MemoryImporter)
    suspend fun importMemoryFromFile(context: android.content.Context, filePath: String) {
        MemoryImporter.importFromFile(context, filePath, dao, currentAccountId, currentDeviceId)
    }

    // Simple diagnostics: counts of memories and messages for current context
    suspend fun diagnostics(): Map<String, Long> {
        val memories = try { dao.getAllCulturalMemories().count().toLong() } catch (e: Exception) { 0L }
        val messages = try { dao.getRecentMessages(1000).size.toLong() } catch (e: Exception) { 0L }
        return mapOf(
            "total_memories" to memories,
            "recent_messages_sample" to messages,
            "account_id" to 0L
        )
    }

    suspend fun addOrUpdateMemory(category: MemoryCategory, key: String, content: String) {
        val existing = dao.getCulturalMemoryByKey(key)
        if (existing != null) {
            dao.updateCulturalMemory(
                existing.copy(
                    content = content,
                    timesUsed = existing.timesUsed + 1,
                    lastMentioned = System.currentTimeMillis()
                )
            )
        } else {
            dao.insertCulturalMemory(
                CulturalMemoryEntity(
                    accountId = currentAccountId,
                    deviceId = currentDeviceId,
                    category = category.name,
                    key = key,
                    content = content,
                    timesUsed = 1,
                    lastMentioned = System.currentTimeMillis()
                )
            )
        }
    }

    // Account management wrappers (thin): create account, request otp, verify otp
    suspend fun createAccount(email: String?, phone: String?): String {
        val manager = AccountManager(dao)
        val acc = manager.createAccount(email, phone)
        // persist account id in a cultural memory entry for discoverability
        addOrUpdateMemory(MemoryCategory.CALLBACK, "account_${acc.id}", "account:${acc.id}")
        return acc.id
    }

    suspend fun requestOtp(accountId: String): String {
        val manager = AccountManager(dao)
        return manager.requestOtpFor(accountId)
    }

    suspend fun verifyOtp(accountId: String, otp: String): Boolean {
        val manager = AccountManager(dao)
        return manager.verifyOtp(accountId, otp)
    }

    /**
     * Basic explanation-based pattern learner for V0.1:
     * - Identifies recurrent nicknames (e.g. "papi", "jefe", "carnal", "compa")
     * - Identifies custom characters or recurring names (e.g. "Protipirugolfo", "mi jefe", "el don")
     * - Detects user expressions or favorite terms
     * - Increments interaction count
     */
    suspend fun analyzeAndLearn(userMessage: String) {
        val lower = userMessage.lowercase().trim()
        val currentProfile = getProfile()

        // Increment interaction count
        val updatedProfile = currentProfile.copy(
            interactionCount = currentProfile.interactionCount + 1,
            lastInteraction = System.currentTimeMillis()
        )
        saveProfile(updatedProfile)

        // 1. Detect nicknames
        val nicknamePatterns = listOf(
            Regex("""me llamo\s+([a-zA-ZáéíóúÁÉÍÓÚñÑ]+)"""),
            Regex("""dime\s+([a-zA-ZáéíóúÁÉÍÓÚñÑ]+)"""),
            Regex("""mi apodo es\s+([a-zA-ZáéíóúÁÉÍÓÚñÑ]+)""")
        )
        for (pattern in nicknamePatterns) {
            val match = pattern.find(lower)
            if (match != null) {
                val newNick = match.groupValues[1].replaceFirstChar { it.uppercase() }
                if (newNick.length in 2..20) {
                    saveProfile(updatedProfile.copy(userNickname = newNick))
                    addOrUpdateMemory(MemoryCategory.NICKNAME, "apodo_usuario", "El usuario prefiere que le digan '$newNick'")
                    return
                }
            }
        }

        // Common colloquial user terms
        val commonPraise = listOf("papi", "carnal", "jefe", "compa", "bro", "valedor", "camarada", "primo")
        for (term in commonPraise) {
            if (lower.contains(term)) {
                addOrUpdateMemory(MemoryCategory.FAVORITE_TERM, "termino_$term", "El usuario utiliza con frecuencia la expresión '$term'")
            }
        }

        // Detect recurring characters mentioned by user
        val characterRegex = Regex("""(don|doña|el licenciado|lic|el profe|el inge|protipirugolfo|el vecino)\s+([a-zA-ZáéíóúÁÉÍÓÚñÑ]+)?""")
        val charMatch = characterRegex.find(lower)
        if (charMatch != null) {
            val characterName = charMatch.value.trim().replaceFirstChar { it.uppercase() }
            addOrUpdateMemory(MemoryCategory.RECURRING_CHARACTER, "personaje_${characterName.take(15)}", "Personaje recurrente mencionado: $characterName")
        }

        // Detect laughter / preferred humor
        if (lower.contains("jajaja") || lower.contains("xd") || lower.contains("me dio risa") || lower.contains("buenisimo")) {
            addOrUpdateMemory(MemoryCategory.PREFERRED_HUMOR_PATTERN, "humor_activo", "Al usuario le agrada el humor absurdo y las analogías")
        }
    }
}
