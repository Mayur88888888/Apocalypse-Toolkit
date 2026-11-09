package com.example.ui.tools

import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.data.knowledge.KnowledgeData
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun EmergencySosToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val contacts by viewModel.repository.allContacts.collectAsState(initial = emptyList())
    val currentLocation by viewModel.sensorHelper.currentLocation.collectAsState()

    var isDeadmanActive by remember { mutableStateOf(false) }
    var deadmanTimerHours by remember { mutableStateOf(24) }
    var showPanicWipeConfirm by remember { mutableStateOf(false) }

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
            color = EmergencyRed
        )
        Text(
            text = tool.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        when (tool.id) {
            99 -> { // One-Tap SOS
                Button(
                    onClick = { viewModel.triggerOneTapSos(contacts) },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Text("TRIGGER FULL ONE-TAP SOS ALERT", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
                Button(
                    onClick = { viewModel.stopSos() },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CANCEL ACTIVE SOS")
                }
            }
            100 -> { // Emergency Speed-Dial
                Text("One-Touch Emergency Speed Dial:", color = TextPrimary, fontWeight = FontWeight.Bold)
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("DIAL 911 / 112 EMERGENCY SERVICES")
                }
                contacts.forEach { contact ->
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("DIAL ${contact.name.uppercase()} (${contact.phone})")
                    }
                }
            }
            101 -> { // Medical ID Lock Card
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.25f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("LOCK SCREEN MEDICAL ID CARD", color = EmergencyRed, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("BLOOD TYPE: O POSITIVE (O+)", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("ALLERGIES: PENICILLIN, BEE VENOM", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Text("ICE CONTACT: ${contacts.firstOrNull()?.name ?: "Primary ICE Contact"} (${contacts.firstOrNull()?.phone ?: "555-0199"})", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            102 -> { // Offline Emergency Numbers
                Text("Global Regional Emergency Numbers:", color = TextPrimary, fontWeight = FontWeight.Bold)
                LazyColumn(modifier = Modifier.height(180.dp).fillMaxWidth()) {
                    items(KnowledgeData.emergencyNumbers) { (region, num) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(region, color = TextPrimary, fontSize = 13.sp)
                            Text(num, color = EmergencyRed, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                        HorizontalDivider(color = BorderColor)
                    }
                }
            }
            103 -> { // "I'm Safe" SMS Broadcast
                val safeMsg = "I AM SAFE. Current GPS Location: Lat ${currentLocation?.latitude ?: 37.7749}, Long ${currentLocation?.longitude ?: -122.4194}"
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("BROADCAST PAYLOAD:", color = TacticalAmber, style = MaterialTheme.typography.labelSmall)
                        Text(safeMsg, color = TextPrimary, fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = {
                        try {
                            val smsManager = context.getSystemService(SmsManager::class.java)
                            contacts.forEach { c -> smsManager.sendTextMessage(c.phone, null, safeMsg, null, null) }
                        } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SEND 'I'M SAFE' SMS TO ALL CONTACTS")
                }
            }
            104 -> { // Distress Signal Guide
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("GROUND-TO-AIR VISUAL SYMBOLS:", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Text("• V : Require Assistance", color = TextPrimary, fontSize = 12.sp)
                        Text("• X : Require Medical Assistance", color = TextPrimary, fontSize = 12.sp)
                        Text("• N : No / Negative", color = TextPrimary, fontSize = 12.sp)
                        Text("• Y : Yes / Affirmative", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            105 -> { // Emergency Whistle Pattern Guide
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("WHISTLE DISTRESS SIGNALS:", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Text("• 3 Blasts (1 sec each): Universal Distress Signal.", color = TextPrimary, fontSize = 12.sp)
                        Text("• 2 Blasts: Response / 'Call Back' Signal.", color = TextPrimary, fontSize = 12.sp)
                        Text("• 1 Blast: Location marker.", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            106 -> { // Panic Wipe
                if (!showPanicWipeConfirm) {
                    Button(
                        onClick = { showPanicWipeConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("INITIATE PANIC WIPE (DELETE DB NOTES & CHATS)", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.25f))) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("CONFIRM WIPING ALL DB NOTES & CHAT LOGS?", color = EmergencyRed, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.panicWipe()
                                        showPanicWipeConfirm = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                                ) {
                                    Text("CONFIRM WIPE")
                                }
                                Button(onClick = { showPanicWipeConfirm = false }) {
                                    Text("CANCEL")
                                }
                            }
                        }
                    }
                }
            }
            107 -> { // Dead-Man Switch
                Text("Check-in Interval: $deadmanTimerHours Hours", color = TextPrimary, fontWeight = FontWeight.Bold)
                Slider(value = deadmanTimerHours.toFloat(), onValueChange = { deadmanTimerHours = it.toInt() }, valueRange = 1f..72f, steps = 71)
                Button(
                    onClick = { isDeadmanActive = !isDeadmanActive },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDeadmanActive) EmergencyRed else CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isDeadmanActive) "DEAD-MAN SWITCH ACTIVE (RESET TIMER)" else "ENABLE DEAD-MAN CHECK-IN TIMER")
                }
            }
            108 -> { // Emergency Battery-Saving Mode
                Button(
                    onClick = { viewModel.toggleHighContrast(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ENABLE EXTREME BATTERY-SAVING DIM MODE")
                }
            }
        }
    }
}
