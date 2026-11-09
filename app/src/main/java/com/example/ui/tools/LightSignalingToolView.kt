package com.example.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LightSignalingToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val isFlashlightOn by viewModel.isFlashlightOn.collectAsState()
    val strobeSpeedHz by viewModel.strobeSpeedHz.collectAsState()
    var customText by remember { mutableStateOf("SOS") }
    var selectedScreenColor by remember { mutableStateOf(Color.White) }
    var isScreenLightActive by remember { mutableStateOf(false) }

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
            1 -> { // Flashlight Toggle
                Button(
                    onClick = { viewModel.toggleFlashlight(!isFlashlightOn) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFlashlightOn) SurvivalOrange else DarkSurfaceVariant
                    ),
                    modifier = Modifier
                        .size(120.dp)
                        .testTag("flashlight_toggle_btn"),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (isFlashlightOn) "ON" else "OFF",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isFlashlightOn) Color.Black else TextPrimary
                    )
                }
            }
            2 -> { // Strobe Mode
                Text("Strobe Frequency: ${strobeSpeedHz.toInt()} Hz", color = TextPrimary)
                Slider(
                    value = strobeSpeedHz,
                    onValueChange = { viewModel.startStrobe(it) },
                    valueRange = 1f..25f,
                    steps = 24,
                    colors = SliderDefaults.colors(thumbColor = SurvivalOrange, activeTrackColor = SurvivalOrange)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.startStrobe(strobeSpeedHz) },
                        colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange)
                    ) {
                        Text("START STROBE")
                    }
                    Button(
                        onClick = { viewModel.stopStrobe() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                    ) {
                        Text("STOP")
                    }
                }
            }
            3, 4 -> { // SOS Morse & Custom Morse
                OutlinedTextField(
                    value = customText,
                    onValueChange = { customText = it },
                    label = { Text("Morse Text Signal") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = SurvivalOrange
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.blinkMorseText(customText) },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("BLINK MORSE SIGNAL")
                }
            }
            5, 6, 7 -> { // Screen light / Colored light / Red Night Mode
                Text("Screen Light Mode", color = TextPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val colorsList = listOf(Color.White, Color.Red, Color.Green, Color.Blue, Color.Yellow)
                    colorsList.forEach { c ->
                        IconButton(
                            onClick = {
                                selectedScreenColor = c
                                isScreenLightActive = true
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(c, CircleShape)
                        ) {}
                    }
                }
                if (isScreenLightActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(selectedScreenColor)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = { isScreenLightActive = false }) {
                            Text("TAP TO CLOSE SCREEN LIGHT", color = Color.Black)
                        }
                    }
                }
            }
            else -> {
                Button(
                    onClick = { viewModel.startStrobe(10f) },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ACTIVATE EMERGENCY SIGNAL BEACON")
                }
            }
        }
    }
}
