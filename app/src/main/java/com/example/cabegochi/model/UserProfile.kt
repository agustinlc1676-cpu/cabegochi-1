package com.example.cabegochi.model

data class UserProfile(
    val id: Long = 1,
    val userNickname: String = "Papi",
    val cabegochiName: String = "Traviesón",
    val selectedCharacter: CabegochiCharacter = CabegochiCharacter.TRAVIESON,
    val selectedVoiceName: String? = null,
    val speechRate: Float = 1.0f,
    val speechPitch: Float = 1.0f,
    val autoSpeak: Boolean = true,
    val cotorreoLevel: CotorreoLevel = CotorreoLevel.CABEGOCHI_NORMAL,
    val interactionCount: Int = 0,
    val firstLaunchCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastInteraction: Long = System.currentTimeMillis()
)
