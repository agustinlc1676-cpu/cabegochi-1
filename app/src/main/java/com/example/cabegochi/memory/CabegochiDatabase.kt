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
    version = 2,
    exportSchema = false
)
abstract class CabegochiDatabase : RoomDatabase() {
    abstract fun cabegochiDao(): CabegochiDao

    companion object {
        @Volatile
        private var INSTANCE: CabegochiDatabase? = null

        fun getDatabase(context: Context): CabegochiDatabase {
            return INSTANCE ?: synchronized(this) {
                val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
                    override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                        // Add new columns with default values so existing data keeps working
                        database.execSQL("ALTER TABLE user_profile ADD COLUMN accountId TEXT DEFAULT 'local' NOT NULL")
                        database.execSQL("ALTER TABLE user_profile ADD COLUMN deviceId TEXT DEFAULT 'local' NOT NULL")
                        database.execSQL("ALTER TABLE cultural_memory ADD COLUMN accountId TEXT DEFAULT 'local' NOT NULL")
                        database.execSQL("ALTER TABLE cultural_memory ADD COLUMN deviceId TEXT DEFAULT 'local' NOT NULL")
                        database.execSQL("ALTER TABLE chat_messages ADD COLUMN accountId TEXT DEFAULT 'local' NOT NULL")
                        database.execSQL("ALTER TABLE chat_messages ADD COLUMN deviceId TEXT DEFAULT 'local' NOT NULL")
                    }
                }

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CabegochiDatabase::class.java,
                    "cabegochi_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
