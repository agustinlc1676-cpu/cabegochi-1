package com.example.cabegochi.memory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CabegochiDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    // Cultural Memory
    @Query("SELECT * FROM cultural_memory ORDER BY timesUsed DESC, lastMentioned DESC")
    fun getAllCulturalMemoriesFlow(): Flow<List<CulturalMemoryEntity>>

    @Query("SELECT * FROM cultural_memory ORDER BY timesUsed DESC, lastMentioned DESC")
    suspend fun getAllCulturalMemories(): List<CulturalMemoryEntity>

    @Query("SELECT * FROM cultural_memory WHERE `key` = :key LIMIT 1")
    suspend fun getCulturalMemoryByKey(key: String): CulturalMemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCulturalMemory(memory: CulturalMemoryEntity)

    @Update
    suspend fun updateCulturalMemory(memory: CulturalMemoryEntity)

    @Query("DELETE FROM cultural_memory WHERE id = :id")
    suspend fun deleteCulturalMemory(id: Long)

    @Query("DELETE FROM cultural_memory")
    suspend fun clearAllCulturalMemories()

    // Chat History
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}
