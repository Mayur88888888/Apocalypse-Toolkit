package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WaypointDao {
    @Query("SELECT * FROM waypoints ORDER BY timestamp DESC")
    fun getAllWaypoints(): Flow<List<WaypointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaypoint(waypoint: WaypointEntity): Long

    @Query("DELETE FROM waypoints WHERE id = :id")
    suspend fun deleteWaypoint(id: Long)

    @Query("DELETE FROM waypoints")
    suspend fun deleteAll()
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()
}

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklists WHERE category = :category ORDER BY id ASC")
    fun getChecklist(category: String): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM checklists ORDER BY category ASC")
    fun getAllChecklists(): Flow<List<ChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItemEntity): Long

    @Update
    suspend fun updateItem(item: ChecklistItemEntity)

    @Query("DELETE FROM checklists WHERE id = :id")
    suspend fun deleteItem(id: Long)
}

@Dao
interface InjuryLogDao {
    @Query("SELECT * FROM injury_logs ORDER BY timestamp DESC")
    fun getAllInjuryLogs(): Flow<List<InjuryLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: InjuryLogEntity): Long

    @Query("DELETE FROM injury_logs WHERE id = :id")
    suspend fun deleteLog(id: Long)
}

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<EmergencyContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContactEntity): Long

    @Query("DELETE FROM emergency_contacts WHERE id = :id")
    suspend fun deleteContact(id: Long)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE channel = :channel ORDER BY timestamp ASC")
    fun getChannelMessages(channel: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()
}

@Dao
interface HealthLogDao {
    @Query("SELECT * FROM health_logs WHERE logType = :type ORDER BY timestamp DESC")
    fun getLogsByType(type: String): Flow<List<HealthLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HealthLogEntity): Long
}

@Dao
interface SolarChargeDao {
    @Query("SELECT * FROM solar_charge_logs ORDER BY timestamp DESC")
    fun getAllSolarLogs(): Flow<List<SolarChargeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SolarChargeEntity): Long
}
