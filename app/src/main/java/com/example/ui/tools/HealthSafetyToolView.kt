package com.example.ui.tools

import android.telephony.SmsManager
import androidx.compose.animation.core.animateFloatAsState
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = tool.name,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = SurvivalOrange
        )
        Text(
            text = tool.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        when (tool.id) {
            70 -> { // CPR Metronome 100-120bpm
                Text("CPR Compression Speed: $cprBpm BPM", color = TextPrimary, fontWeight = FontWeight.Bold)
                Slider(
                    value = cprBpm.toFloat(),
                    onValueChange = { cprBpm = it.toInt() },
                    valueRange = 100f..120f,
                    steps = 20,
                    colors = SliderDefaults.colors(thumbColor = EmergencyRed, activeTrackColor = EmergencyRed)
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            if (metronomeBeat) EmergencyRed else DarkSurfaceVariant,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PUSH",
                        color = if (metronomeBeat) Color.White else TextSecondary,
                        fontWeight = FontWeight.Black
                    )
                }
                Button(
                    onClick = {
                        if (isMetronomeActive) {
                            viewModel.audioGenerator.stop()
                            isMetronomeActive = false
                        } else {
                            viewModel.audioGenerator.startCprMetronome(cprBpm, scope) {
                                metronomeBeat = !metronomeBeat
                            }
                            isMetronomeActive = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMetronomeActive) EmergencyRed else CyberGreen
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("cpr_metronome_btn")
                ) {
                    Text(if (isMetronomeActive) "STOP CPR METRONOME" else "START CPR METRONOME (100-120 BPM)")
                }
            }
            73, 74 -> { // Emergency Contacts & Blood Card
                Text("Saved Contacts (${contacts.size}):", color = TextPrimary, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { contactName = it },
                        label = { Text("Name") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
                Button(
                    onClick = {
                        if (contactName.isNotBlank() && contactPhone.isNotBlank()) {
                            val c = EmergencyContactEntity(name = contactName, relationship = "ICE", phone = contactPhone, bloodType = "O+")
                            kotlinx.coroutines.runBlocking { viewModel.repository.addContact(c) }
                            contactName = ""
                            contactPhone = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ADD CONTACT TO ROOM DB")
                }
                LazyColumn(modifier = Modifier.height(140.dp).fillMaxWidth()) {
                    items(contacts) { c ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${c.name} (${c.relationship})", color = TextPrimary)
                            Text(c.phone, color = TacticalAmber, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = BorderColor)
                    }
                }
            }
            78 -> { // Panic Button SMS
                Button(
                    onClick = {
                        try {
                            val smsManager = context.getSystemService(SmsManager::class.java)
                            contacts.forEach { contact ->
                                smsManager.sendTextMessage(contact.phone, null, "EMERGENCY SOS: I need immediate assistance!", null, null)
                            }
                        } catch (e: Exception) {
                            // Graceful fallback
                        }
                        viewModel.triggerOneTapSos(contacts)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.fillMaxWidth().testTag("panic_sms_btn")
                ) {
                    Text("TRIGGER PANIC BUTTON SMS ALERT")
                }
            }
            else -> {
                Text("Health Logger Active", color = CyberGreen)
            }
        }
    }
}
