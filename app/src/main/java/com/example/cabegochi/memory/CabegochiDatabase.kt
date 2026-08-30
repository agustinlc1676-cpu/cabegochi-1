package com.example.cabegochi.memory

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        CulturalMemoryEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CabegochiDatabase : RoomDatabase() {
    abstract fun cabegochiDao(): CabegochiDao

    companion object {
        @Volatile
        private var INSTANCE: CabegochiDatabase? = null

        fun getDatabase(context: Context): CabegochiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CabegochiDatabase::class.java,
                    "cabegochi_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
