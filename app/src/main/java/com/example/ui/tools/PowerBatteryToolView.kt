package com.example.ui.tools

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
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

@Composable
fun PowerBatteryToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val batteryStats = remember { viewModel.sensorHelper.getBatteryInfo() }
    val solarLogs by viewModel.repository.solarLogs.collectAsState(initial = emptyList())

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
            text = tool.name,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = SurvivalOrange
        )
        Text(
            text = tool.description,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("BATTERY LEVEL: ${batteryStats.levelPercent}%", color = CyberGreen, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text("CHARGING STATUS: ${if (batteryStats.isCharging) "Charging" else "Discharging"}", color = TextPrimary)
                Text("ESTIMATED TIME REMAINING: ${(batteryStats.levelPercent * 0.18).let { String.format("%.1f Hours", it) }}", color = TacticalAmber)
            }
        }

        when (tool.id) {
            80 -> { // Battery saver mode toggle
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OPEN SYSTEM BATTERY SAVER SETTINGS")
                }
            }
            85 -> { // Solar charge log
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = solarMins,
                        onValueChange = { solarMins = it },
                        label = { Text("Duration (Mins)") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = solarWh,
                        onValueChange = { solarWh = it },
                        label = { Text("Est. Wh") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
                Button(
                    onClick = {
                        val log = SolarChargeEntity(
                            durationMinutes = solarMins.toIntOrNull() ?: 60,
                            estimatedWh = solarWh.toFloatOrNull() ?: 15.0f
                        )
                        kotlinx.coroutines.runBlocking { viewModel.repository.addSolarLog(log) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LOG SOLAR CHARGE SESSION TO DB")
                }
            }
            else -> {
                Text("Battery Health & Diagnostics OK", color = CyberGreen)
            }
        }
    }
}
