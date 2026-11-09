package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "apocalypse_settings")

class SurvivalRepository(private val db: AppDatabase, private val context: Context) {
    // Waypoints
    val allWaypoints: Flow<List<WaypointEntity>> = db.waypointDao().getAllWaypoints()
    suspend fun addWaypoint(wp: WaypointEntity) = db.waypointDao().insertWaypoint(wp)
    suspend fun deleteWaypoint(id: Long) = db.waypointDao().deleteWaypoint(id)

    // Notes
    val allNotes: Flow<List<NoteEntity>> = db.noteDao().getAllNotes()
    suspend fun addNote(note: NoteEntity) = db.noteDao().insertNote(note)
    suspend fun deleteNote(id: Long) = db.noteDao().deleteNote(id)
    suspend fun deleteAllNotes() = db.noteDao().deleteAll()

    // Checklists
    val allChecklists: Flow<List<ChecklistItemEntity>> = db.checklistDao().getAllChecklists()
    fun getChecklist(category: String): Flow<List<ChecklistItemEntity>> = db.checklistDao().getChecklist(category)
    suspend fun addChecklistItem(item: ChecklistItemEntity) = db.checklistDao().insertItem(item)
    suspend fun updateChecklistItem(item: ChecklistItemEntity) = db.checklistDao().updateItem(item)
    suspend fun deleteChecklistItem(id: Long) = db.checklistDao().deleteItem(id)

    // Injury Logs
    val allInjuryLogs: Flow<List<InjuryLogEntity>> = db.injuryLogDao().getAllInjuryLogs()
    suspend fun addInjuryLog(log: InjuryLogEntity) = db.injuryLogDao().insertLog(log)
    suspend fun deleteInjuryLog(id: Long) = db.injuryLogDao().deleteLog(id)

    // Contacts
    val allContacts: Flow<List<EmergencyContactEntity>> = db.emergencyContactDao().getAllContacts()
    suspend fun addContact(c: EmergencyContactEntity) = db.emergencyContactDao().insertContact(c)
    suspend fun deleteContact(id: Long) = db.emergencyContactDao().deleteContact(id)

    // Chat
    val allChatMessages: Flow<List<ChatMessageEntity>> = db.chatMessageDao().getAllMessages()
    fun getChannelMessages(channel: String): Flow<List<ChatMessageEntity>> = db.chatMessageDao().getChannelMessages(channel)
    suspend fun addChatMessage(msg: ChatMessageEntity) = db.chatMessageDao().insertMessage(msg)
    suspend fun clearChatHistory() = db.chatMessageDao().clearAll()

    // Health
    fun getHealthLogs(type: String): Flow<List<HealthLogEntity>> = db.healthLogDao().getLogsByType(type)
    suspend fun addHealthLog(log: HealthLogEntity) = db.healthLogDao().insertLog(log)

    // Solar
    val solarLogs: Flow<List<SolarChargeEntity>> = db.solarChargeDao().getAllSolarLogs()
    suspend fun addSolarLog(log: SolarChargeEntity) = db.solarChargeDao().insertLog(log)

    // DataStore Settings Keys
    private val HIGH_CONTRAST_KEY = booleanPreferencesKey("high_contrast")
    private val LARGE_TEXT_KEY = booleanPreferencesKey("large_text")
    private val PIN_LOCK_KEY = stringPreferencesKey("pin_lock")
    private val DEADMAN_HOURS_KEY = stringPreferencesKey("deadman_hours")

    val highContrastFlow: Flow<Boolean> = context.dataStore.data.map { it[HIGH_CONTRAST_KEY] ?: false }
    val largeTextFlow: Flow<Boolean> = context.dataStore.data.map { it[LARGE_TEXT_KEY] ?: false }
    val pinLockFlow: Flow<String> = context.dataStore.data.map { it[PIN_LOCK_KEY] ?: "" }
    val deadmanHoursFlow: Flow<Int> = context.dataStore.data.map { (it[DEADMAN_HOURS_KEY] ?: "24").toIntOrNull() ?: 24 }

    suspend fun setHighContrast(enabled: Boolean) {
        context.dataStore.edit { it[HIGH_CONTRAST_KEY] = enabled }
    }

    suspend fun setLargeText(enabled: Boolean) {
        context.dataStore.edit { it[LARGE_TEXT_KEY] = enabled }
    }

    suspend fun setPinLock(pin: String) {
        context.dataStore.edit { it[PIN_LOCK_KEY] = pin }
    }

    suspend fun setDeadmanHours(hours: Int) {
        context.dataStore.edit { it[DEADMAN_HOURS_KEY] = hours.toString() }
    }
}
