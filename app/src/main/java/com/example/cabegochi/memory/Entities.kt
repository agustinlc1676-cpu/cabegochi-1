package com.example.cabegochi.memory

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Long = 1,
    val userNickname: String = "Papi",
    val cabegochiName: String = "Traviesón",
    val selectedCharacterId: String = "travieson",
    val selectedVoiceName: String? = null,
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val autoSpeak: Boolean = true,
    val cotorreoLevelInt: Int = 3,
    val interactionCount: Int = 0,
    val firstLaunchCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastInteraction: Long = System.currentTimeMillis()
)

@Entity(tableName = "cultural_memory")
data class CulturalMemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val key: String,
    val content: String,
    val timesUsed: Int = 1,
    val lastMentioned: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val characterMood: String? = null
)
