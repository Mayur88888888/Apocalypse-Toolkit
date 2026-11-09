package com.example.ui.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ToolItem
import com.example.data.knowledge.KnowledgeData
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun EmergencySosToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
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
            text = tool.name,
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
                    onClick = { viewModel.triggerOneTapSos(emptyList()) },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Text("TRIGGER FULL ONE-TAP SOS ALERT", fontWeight = FontWeight.Black)
                }
                Button(
                    onClick = { viewModel.stopSos() },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CANCEL SOS ALERT")
                }
            }
            102 -> { // Offline Emergency Numbers
                Text("Global Regional Emergency Numbers:", color = TextPrimary, fontWeight = FontWeight.Bold)
                LazyColumn(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                    items(KnowledgeData.emergencyNumbers) { (region, num) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(region, color = TextPrimary)
                            Text(num, color = EmergencyRed, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = BorderColor)
                    }
                }
            }
            106 -> { // Panic Wipe
                if (!showPanicWipeConfirm) {
                    Button(
                        onClick = { showPanicWipeConfirm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("INITIATE PANIC WIPE")
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = EmergencyRed.copy(alpha = 0.2f))) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("CONFIRM WIPING ALL DB NOTES & CHAT?", color = EmergencyRed, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
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
            107 -> { // Dead-man Switch
                Text("Dead-Man Check-in Interval: $deadmanTimerHours Hours", color = TextPrimary, fontWeight = FontWeight.Bold)
                Slider(
                    value = deadmanTimerHours.toFloat(),
                    onValueChange = { deadmanTimerHours = it.toInt() },
                    valueRange = 1f..72f,
                    steps = 71
                )
                Button(
                    onClick = { isDeadmanActive = !isDeadmanActive },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDeadmanActive) EmergencyRed else CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isDeadmanActive) "DEAD-MAN SWITCH ACTIVE (RESET)" else "ENABLE DEAD-MAN SWITCH")
                }
            }
            else -> {
                Button(
                    onClick = { viewModel.triggerOneTapSos(emptyList()) },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ACTIVATE EMERGENCY BEACON")
                }
            }
        }
    }
}
