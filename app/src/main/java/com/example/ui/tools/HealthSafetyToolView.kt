package com.example.ui.tools

import android.telephony.SmsManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.data.db.EmergencyContactEntity
import com.example.data.db.InjuryLogEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HealthSafetyToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val contacts by viewModel.repository.allContacts.collectAsState(initial = emptyList())
    val injuryLogs by viewModel.repository.allInjuryLogs.collectAsState(initial = emptyList())

    var isMetronomeActive by remember { mutableStateOf(false) }
    var cprBpm by remember { mutableStateOf(110) }
    var metronomeBeat by remember { mutableStateOf(false) }

    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }

    var injuryTitle by remember { mutableStateOf("") }
    var injuryLoc by remember { mutableStateOf("") }
    var injurySeverity by remember { mutableStateOf("Moderate") }

    var tempReading by remember { mutableStateOf("37.0") }
    var waterLoggedMl by remember { mutableStateOf(750) }

    var woundTimerMins by remember { mutableStateOf(30) }
    var isWoundTimerActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "#${tool.id}. ${tool.name}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = SurvivalOrange
        )
        Text(
            text = tool.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        when (tool.id) {
            69 -> { // First-Aid Checklist
                val items = remember { mutableStateListOf("Sterile Gauze", "Tourniquet", "Antiseptic Wipes", "Burn Cream", "Trauma Shears", "Adhesive Bandages", "Nitrile Gloves") }
                Text("Emergency First-Aid Supplies Checklist:", color = TextPrimary, fontWeight = FontWeight.Bold)
                LazyColumn(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                    items(items) { item ->
                        var checked by remember { mutableStateOf(false) }
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = checked, onCheckedChange = { checked = it })
                            Text(item, color = if (checked) TextSecondary else TextPrimary)
                        }
                    }
                }
            }
            70 -> { // CPR Metronome 100-120bpm
                Text("CPR Compression Rhythm: $cprBpm BPM", color = TextPrimary, fontWeight = FontWeight.Bold)
                Slider(
                    value = cprBpm.toFloat(),
                    onValueChange = { cprBpm = it.toInt() },
                    valueRange = 100f..120f,
                    steps = 20,
                    colors = SliderDefaults.colors(thumbColor = EmergencyRed, activeTrackColor = EmergencyRed)
                )
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(if (metronomeBeat) EmergencyRed else DarkSurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("PUSH", color = if (metronomeBeat) Color.White else TextSecondary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
                Button(
                    onClick = {
                        if (isMetronomeActive) {
                            viewModel.audioGenerator.stop()
                            isMetronomeActive = false
                        } else {
                            viewModel.audioGenerator.startCprMetronome(cprBpm, scope) { metronomeBeat = !metronomeBeat }
                            isMetronomeActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isMetronomeActive) EmergencyRed else CyberGreen),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(if (isMetronomeActive) "STOP CPR METRONOME" else "START CPR METRONOME (100-120 BPM)")
                }
            }
            71 -> { // Injury Log & Medical Notes
                OutlinedTextField(value = injuryTitle, onValueChange = { injuryTitle = it }, label = { Text("Injury Description") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = injuryLoc, onValueChange = { injuryLoc = it }, label = { Text("Body Location") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary), modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {
                        if (injuryTitle.isNotBlank()) {
                            val log = InjuryLogEntity(title = injuryTitle, bodyLocation = injuryLoc, severity = injurySeverity, notes = "Logged in field")
                            scope.launch { viewModel.repository.addInjuryLog(log) }
                            injuryTitle = ""
                            injuryLoc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LOG INJURY RECORD TO ROOM DB")
                }
                LazyColumn(modifier = Modifier.height(100.dp).fillMaxWidth()) {
                    items(injuryLogs) { log ->
                        Text("• ${log.title} (${log.bodyLocation}) - ${log.severity}", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            72 -> { // Medication Reminder
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("MEDICATION ALARM REMINDERS", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Antibiotic (Amoxicillin 500mg) - Every 8 Hours", color = TextPrimary, fontSize = 12.sp)
                        Text("• Pain Relief (Ibuprofen 400mg) - Every 6 Hours", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            73 -> { // Emergency Contacts
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = contactName, onValueChange = { contactName = it }, label = { Text("Name") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = contactPhone, onValueChange = { contactPhone = it }, label = { Text("Phone") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                }
                Button(
                    onClick = {
                        if (contactName.isNotBlank() && contactPhone.isNotBlank()) {
                            val c = EmergencyContactEntity(name = contactName, relationship = "ICE", phone = contactPhone, bloodType = "O+")
                            scope.launch { viewModel.repository.addContact(c) }
                            contactName = ""
                            contactPhone = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ADD CONTACT TO ROOM DB")
                }
                LazyColumn(modifier = Modifier.height(100.dp).fillMaxWidth()) {
                    items(contacts) { c ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${c.name} (${c.relationship})", color = TextPrimary)
                            Text(c.phone, color = TacticalAmber, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            74 -> { // Blood Type & Allergy Card
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.2f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("EMERGENCY MEDICAL ID CARD", color = EmergencyRed, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("BLOOD TYPE: O POSITIVE (O+)", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("ALLERGIES: PENICILLIN, BEE STINGS", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Text("CONDITIONS: NONE", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            75 -> { // Body Temperature Log
                OutlinedTextField(value = tempReading, onValueChange = { tempReading = it }, label = { Text("Temperature (°C)") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary), modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LOG TEMPERATURE READING (${tempReading} °C)")
                }
            }
            76 -> { // Water Intake Tracker
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DAILY WATER INTAKE", color = SignalCyan, style = MaterialTheme.typography.labelSmall)
                        Text("$waterLoggedMl / 2500 ML", color = TextPrimary, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { waterLoggedMl = (waterLoggedMl + 250).coerceAtMost(3500) },
                            colors = ButtonDefaults.buttonColors(containerColor = SignalCyan)
                        ) {
                            Text("+250 ML GLASS", color = Color.Black)
                        }
                    }
                }
            }
            77 -> { // Wound Care Timer
                Text("Bandage Change Interval: $woundTimerMins Minutes", color = TextPrimary, fontWeight = FontWeight.Bold)
                Slider(value = woundTimerMins.toFloat(), onValueChange = { woundTimerMins = it.toInt() }, valueRange = 15f..120f, steps = 7)
                Button(
                    onClick = { isWoundTimerActive = !isWoundTimerActive },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isWoundTimerActive) EmergencyRed else CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isWoundTimerActive) "STOP WOUND CARE TIMER" else "START DISINFECTION REMINDER")
                }
            }
            78 -> { // Panic Button SMS
                Button(
                    onClick = {
                        try {
                            val smsManager = context.getSystemService(SmsManager::class.java)
                            contacts.forEach { contact ->
                                smsManager.sendTextMessage(contact.phone, null, "EMERGENCY SOS: I need immediate help!", null, null)
                            }
                        } catch (_: Exception) {}
                        viewModel.triggerOneTapSos(contacts)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("TRIGGER PANIC BUTTON SMS ALERT", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
