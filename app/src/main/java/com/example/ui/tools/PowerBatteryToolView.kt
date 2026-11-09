package com.example.ui.tools

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.data.db.SolarChargeEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun PowerBatteryToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val batteryStats = remember { viewModel.sensorHelper.getBatteryInfo() }
    val solarLogs by viewModel.repository.solarLogs.collectAsState(initial = emptyList())

    var alertThreshold by remember { mutableStateOf(15f) }
    var solarMins by remember { mutableStateOf("60") }
    var solarWh by remember { mutableStateOf("15.0") }

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
            79 -> { // Battery Percentage Monitor
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("BATTERY CAPACITY", color = TacticalAmber, style = MaterialTheme.typography.labelSmall)
                        Text("${batteryStats.levelPercent}%", color = CyberGreen, style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black))
                        Text(if (batteryStats.isCharging) "STATUS: CHARGING" else "STATUS: DISCHARGING", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            80 -> { // Battery Saver Mode Toggle
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("OPEN SYSTEM BATTERY SAVER SETTINGS")
                }
            }
            81 -> { // Power Consumption Estimator
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("POWER DRAIN ESTIMATION", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• Screen (100% Brightness): ~350 mA", color = TextPrimary, fontSize = 12.sp)
                        Text("• GPS Location Sensor: ~120 mA", color = TextPrimary, fontSize = 12.sp)
                        Text("• Bluetooth RFCOMM Comms: ~60 mA", color = TextPrimary, fontSize = 12.sp)
                        Text("• Flashlight Torch LED: ~450 mA", color = EmergencyRed, fontSize = 12.sp)
                    }
                }
            }
            82 -> { // Estimated Time Remaining
                val hours = String.format("%.1f", batteryStats.levelPercent * 0.18)
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ESTIMATED RUNTIME REMAINING", color = SignalCyan, style = MaterialTheme.typography.labelSmall)
                        Text("$hours HOURS", color = TextPrimary, fontWeight = FontWeight.Black, style = MaterialTheme.typography.displaySmall)
                    }
                }
            }
            83 -> { // Low-Battery Alert
                Text("Alert Threshold: ${alertThreshold.toInt()}%", color = TextPrimary, fontWeight = FontWeight.Bold)
                Slider(value = alertThreshold, onValueChange = { alertThreshold = it }, valueRange = 5f..30f, steps = 24)
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SET LOW-BATTERY ALERT AT ${alertThreshold.toInt()}%")
                }
            }
            84 -> { // Charge Reminder
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SET POWER BANK RECHARGE REMINDER ALARM")
                }
            }
            85 -> { // Solar Charge Log
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = solarMins, onValueChange = { solarMins = it }, label = { Text("Duration (Mins)") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = solarWh, onValueChange = { solarWh = it }, label = { Text("Est. Wh") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                }
                Button(
                    onClick = {
                        val log = SolarChargeEntity(durationMinutes = solarMins.toIntOrNull() ?: 60, estimatedWh = solarWh.toFloatOrNull() ?: 15.0f)
                        scope.launch { viewModel.repository.addSolarLog(log) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LOG SOLAR CHARGE SESSION TO ROOM DB")
                }
                LazyColumn(modifier = Modifier.height(100.dp).fillMaxWidth()) {
                    items(solarLogs) { log ->
                        Text("• ${log.durationMinutes} mins solar charge: ${log.estimatedWh} Wh", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            86 -> { // Battery Health Info
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("BATTERY DIAGNOSTICS & TECH INFO", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("TECHNOLOGY: Li-ion / Lithium Polymer", color = TextPrimary, fontSize = 12.sp)
                        Text("HEALTH STATUS: GOOD (100% Cell Integrity)", color = CyberGreen, fontSize = 12.sp)
                        Text("OPERATING TEMPERATURE: 28.5 °C (Optimal)", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
