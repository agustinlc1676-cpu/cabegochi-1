package com.example.cabegochi.ai

import com.example.cabegochi.chuchuluco.ChuchulucoEngine

/**
 * Local zero-cost AI provider that executes rules and formulas directly on-device.
 */
class LocalChuchulucoProvider : AIProvider {
    override val providerId: String = "local_chuchuluco"
    override val providerDisplayName: String = "Chuchuluco Engine (Local)"
    override val isFreeTier: Boolean = true

    override suspend fun generateReply(request: AIRequest): AIResponse {
        val reply = ChuchulucoEngine.generateLocalFallbackResponse(
            prompt = request.userMessage,
            character = request.userProfile.selectedCharacter,
            cotorreoLevel = request.userProfile.cotorreoLevel,
            userProfile = request.userProfile,
            culturalMemories = request.culturalMemories
        )
        return AIResponse.Success(reply)
    }
}
