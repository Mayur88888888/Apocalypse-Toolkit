package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WaypointEntity::class,
        NoteEntity::class,
        ChecklistItemEntity::class,
        InjuryLogEntity::class,
        EmergencyContactEntity::class,
        ChatMessageEntity::class,
        HealthLogEntity::class,
        SolarChargeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waypointDao(): WaypointDao
    abstract fun noteDao(): NoteDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun injuryLogDao(): InjuryLogDao
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun healthLogDao(): HealthLogDao
    abstract fun solarChargeDao(): SolarChargeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "apocalypse_toolkit.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
