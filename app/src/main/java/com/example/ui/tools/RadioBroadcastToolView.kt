package com.example.ui.tools

import android.content.Intent
import android.net.Uri
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun RadioBroadcastToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var tunedFreq by remember { mutableStateOf(101.5f) }
    var selectedStreamUrl by remember { mutableStateOf("") }
    var morseTransmitterText by remember { mutableStateOf("MAYDAY MAYDAY") }

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
            26 -> { // FM Radio Tuner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("FM FREQUENCY DIAL", color = TacticalAmber, style = MaterialTheme.typography.labelSmall)
                        Text(text = "${String.format("%.1f", tunedFreq)} MHz", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black), color = TextPrimary)
                    }
                }
                Slider(
                    value = tunedFreq,
                    onValueChange = { tunedFreq = it },
                    valueRange = 87.5f..108.0f,
                    colors = SliderDefaults.colors(thumbColor = SurvivalOrange, activeTrackColor = SurvivalOrange)
                )
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_APP_MUSIC) }
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LAUNCH SYSTEM FM RADIO RECEIVER APP")
                }
            }
            27 -> { // AM Radio Reference & Guide
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("AM BROADCAST SPECTRUM (530 - 1700 kHz)", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• 640 kHz / 1240 kHz: CONELRAD emergency broadcast stations.", color = TextPrimary, fontSize = 12.sp)
                        Text("• AM signals bounce off the ionosphere at night for long distance ground wave reception.", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            28 -> { // Internet Radio Streams
                Text("Pre-configured Emergency Web Streams:", color = TextPrimary, fontWeight = FontWeight.Bold)
                val streams = listOf(
                    "NOAA WX Relay 1" to "https://stream.noaa.gov/live1",
                    "Global Civil Defense Net" to "https://radio.civildefense.org/net",
                    "Shortwave World Broadcast" to "https://shortwave.stream/main"
                )
                streams.forEach { (name, url) ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(name, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text(url, color = TextSecondary, fontSize = 10.sp)
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    try { context.startActivity(intent) } catch (_: Exception) {}
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
                            ) {
                                Text("STREAM")
                            }
                        }
                    }
                }
            }
            29 -> { // NOAA Weather Frequencies
                Text("NOAA Weather Channels (Offline Index):", color = TextPrimary, fontWeight = FontWeight.Bold)
                val noaaChannels = listOf(
                    "WX1" to "162.400 MHz",
                    "WX2" to "162.425 MHz",
                    "WX3" to "162.450 MHz",
                    "WX4" to "162.475 MHz",
                    "WX5" to "162.500 MHz",
                    "WX6" to "162.525 MHz",
                    "WX7" to "162.550 MHz"
                )
                LazyColumn(modifier = Modifier.height(200.dp).fillMaxWidth()) {
                    items(noaaChannels) { (chan, freq) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(chan, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(freq, color = TacticalAmber, fontWeight = FontWeight.Black)
                        }
                        HorizontalDivider(color = BorderColor)
                    }
                }
            }
            30 -> { // Scanner Frequency Reference
                Text("Emergency & Public Safety Bands:", color = TextPrimary, fontWeight = FontWeight.Bold)
                val scannerBands = listOf(
                    "Police Main Dispatch" to "460.025 MHz",
                    "Fire & Rescue Dispatch" to "154.280 MHz",
                    "EMS Air Ambulance" to "155.340 MHz",
                    "Aviation International Emergency" to "121.500 MHz AM",
                    "Marine VHF Emergency Ch 16" to "156.800 MHz",
                    "CB Channel 9 Emergency" to "27.065 MHz AM",
                    "HAM 2m National Calling" to "146.520 MHz FM",
                    "HAM 70cm Calling" to "446.000 MHz FM"
                )
                LazyColumn(modifier = Modifier.height(220.dp).fillMaxWidth()) {
                    items(scannerBands) { (service, freq) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(service, color = TextPrimary, fontSize = 13.sp)
                            Text(freq, color = CyberGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        HorizontalDivider(color = BorderColor)
                    }
                }
            }
            31 -> { // Bluetooth Mesh Link
                Button(
                    onClick = { viewModel.selectCategory(12) },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OPEN BLUETOOTH MESH MESSENGER")
                }
            }
            32 -> { // Wi-Fi Direct Link
                Button(
                    onClick = { viewModel.selectCategory(12) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OPEN WI-FI DIRECT PEER CHAT")
                }
            }
            33 -> { // Wi-Fi Walkie-Talkie Link
                Button(
                    onClick = { viewModel.selectCategory(12) },
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OPEN WI-FI WALKIE-TALKIE")
                }
            }
            34 -> { // Morse Flashlight Transmitter
                OutlinedTextField(
                    value = morseTransmitterText,
                    onValueChange = { morseTransmitterText = it },
                    label = { Text("Optical Morse Payload") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.blinkMorseText(morseTransmitterText) },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("TRANSMIT VISUAL MORSE BEACON")
                }
            }
            35 -> { // Morse Audio Transmitter
                OutlinedTextField(
                    value = morseTransmitterText,
                    onValueChange = { morseTransmitterText = it },
                    label = { Text("Acoustic Morse Payload") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.blinkMorseText(morseTransmitterText) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("TRANSMIT ACOUSTIC MORSE SIGNAL")
                }
            }
        }
    }
}
