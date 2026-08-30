package com.example.cabegochi.model

enum class MessageRole {
    USER,
    CABEGOCHI,
    SYSTEM
}

data class ChatMessage(
    val id: Long = 0,
    val role: MessageRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val characterMood: String? = null,
    val isPlayingAudio: Boolean = false
)
