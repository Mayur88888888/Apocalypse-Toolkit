package com.example.ui.tools

import android.content.Intent
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
import com.example.data.ToolItem
import com.example.data.knowledge.KnowledgeData
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun RadioBroadcastToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val context = LocalContext.current

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

        if (tool.isHardwareDependent) {
            Surface(
                color = TacticalAmber.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "HARDWARE STATUS: ${tool.fallbackInfo}",
                    color = TacticalAmber,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        when (tool.id) {
            26, 27 -> { // FM / AM Radio Tuner
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_APP_MUSIC)
                        }
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LAUNCH SYSTEM RADIO APP INTENT")
                }
            }
            28 -> { // Internet radio streams
                Text("Pre-configured Live Emergency Stream Relays:", color = TextPrimary)
                val streams = listOf(
                    "NOAA Weather Relay 1 (Online)" to "http://stream.noaa.gov/wx1",
                    "Global Emergency Net (Online)" to "http://radio.emergency.org/live",
                    "Shortwave Relay Broadcast" to "http://shortwave.stream/main"
                )
                streams.forEach { (name, url) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(name, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text(url, color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                            }
                            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)) {
                                Text("LISTEN")
                            }
                        }
                    }
                }
            }
            29, 30 -> { // NOAA / Scanner Frequencies
                Text("Emergency & NOAA Frequency Guide (Offline)", color = TextPrimary, fontWeight = FontWeight.Bold)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    val frequencies = listOf(
                        "WX1 (NOAA)" to "162.400 MHz",
                        "WX2 (NOAA)" to "162.425 MHz",
                        "WX3 (NOAA)" to "162.450 MHz",
                        "WX4 (NOAA)" to "162.475 MHz",
                        "WX5 (NOAA)" to "162.500 MHz",
                        "WX6 (NOAA)" to "162.525 MHz",
                        "WX7 (NOAA)" to "162.550 MHz",
                        "CB Emergency Ch 9" to "27.065 MHz AM",
                        "Marine VHF Ch 16" to "156.800 MHz",
                        "FRS Channel 1" to "462.5625 MHz"
                    )
                    items(frequencies) { (chan, freq) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(chan, color = TextPrimary)
                            Text(freq, color = TacticalAmber, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(color = BorderColor)
                    }
                }
            }
            else -> {
                Button(
                    onClick = { viewModel.selectCategory(12) },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OPEN PEER COMMS & WIRELESS TRANSMITTER")
                }
            }
        }
    }
}
