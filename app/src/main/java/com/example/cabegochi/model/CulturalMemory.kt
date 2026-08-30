package com.example.cabegochi.model

enum class MemoryCategory {
    NICKNAME,
    FAVORITE_TERM,
    INVENTED_WORD,
    RECURRING_CHARACTER,
    CALLBACK,
    PREFERRED_HUMOR_PATTERN,
    DISLIKED_PATTERN
}

data class CulturalMemoryCard(
    val id: Long = 0,
    val category: MemoryCategory,
    val key: String,
    val content: String,
    val timesUsed: Int = 1,
    val lastMentioned: Long = System.currentTimeMillis()
)
