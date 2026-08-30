package com.example.cabegochi.ai

import com.example.cabegochi.model.ChatMessage
import com.example.cabegochi.model.CulturalMemoryCard
import com.example.cabegochi.model.UserProfile

data class AIRequest(
    val userMessage: String,
    val userProfile: UserProfile,
    val recentHistory: List<ChatMessage>,
    val culturalMemories: List<CulturalMemoryCard>
)

sealed class AIResponse {
    data class Success(val text: String) : AIResponse()
    data class QuotaExceeded(val message: String) : AIResponse()
    data class Error(val errorMessage: String, val fallbackText: String) : AIResponse()
}

/**
 * Pluggable AI Provider interface.
 * Isolates Cabegochi from concrete AI vendors.
 */
interface AIProvider {
    val providerId: String
    val providerDisplayName: String
    val isFreeTier: Boolean

    suspend fun generateReply(request: AIRequest): AIResponse
}
