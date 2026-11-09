package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waypoints")
data class WaypointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "checklists")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val itemText: String,
    val isChecked: Boolean = false,
    val isCustom: Boolean = false
)

@Entity(tableName = "injury_logs")
data class InjuryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val bodyLocation: String,
    val severity: String,
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val relationship: String,
    val phone: String,
    val bloodType: String = "",
    val notes: String = ""
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderName: String,
    val messageText: String,
    val channel: String = "General",
    val transport: String = "BT/Wi-Fi",
    val timestamp: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean = false
)

@Entity(tableName = "health_logs")
data class HealthLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val logType: String, // TEMP, WATER, WOUND
    val value: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "solar_charge_logs")
data class SolarChargeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,
    val estimatedWh: Float,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
