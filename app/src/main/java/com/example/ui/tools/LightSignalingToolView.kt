package com.example.ui.tools

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Warning
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LightSignalingToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val isFlashlightOn by viewModel.isFlashlightOn.collectAsState()
    val strobeSpeedHz by viewModel.strobeSpeedHz.collectAsState()

    var customMorseText by remember { mutableStateOf("SOS HELP") }
    var selectedScreenColor by remember { mutableStateOf(Color.White) }
    var isFullLightActive by remember { mutableStateOf(false) }
    var beaconIntervalSec by remember { mutableStateOf(3f) }
    var isBeaconRunning by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "wigwag")
    val wigWagState by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wigwag_anim"
    )

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
            1 -> { // Flashlight Toggle
                Button(
                    onClick = { viewModel.toggleFlashlight(!isFlashlightOn) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFlashlightOn) SurvivalOrange else DarkSurfaceVariant
                    ),
                    modifier = Modifier
                        .size(130.dp)
                        .testTag("flashlight_toggle_btn"),
                    shape = CircleShape
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Torch",
                            tint = if (isFlashlightOn) Color.Black else TextPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isFlashlightOn) "TORCH ON" else "TORCH OFF",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isFlashlightOn) Color.Black else TextPrimary
                        )
                    }
                }
            }
            2 -> { // Strobe Mode
                Text("Strobe Frequency: ${strobeSpeedHz.toInt()} Hz", color = TextPrimary, fontWeight = FontWeight.Bold)
                Slider(
                    value = strobeSpeedHz,
                    onValueChange = { viewModel.startStrobe(it) },
                    valueRange = 1f..30f,
                    steps = 29,
                    colors = SliderDefaults.colors(thumbColor = SurvivalOrange, activeTrackColor = SurvivalOrange)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.startStrobe(strobeSpeedHz) },
                        colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("START STROBE")
                    }
                    Button(
                        onClick = { viewModel.stopStrobe() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("STOP")
                    }
                }
            }
            3 -> { // SOS Morse Auto-Blink
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("STANDARD SOS PATTERN: ... --- ...", color = TacticalAmber, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("3 Short (150ms) • 3 Long (450ms) • 3 Short (150ms)", color = TextSecondary, fontSize = 12.sp)
                    }
                }
                Button(
                    onClick = { viewModel.blinkMorseText("SOS") },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("AUTO-BLINK SOS PATTERN", fontWeight = FontWeight.Bold)
                }
            }
            4 -> { // Custom Morse Message Blinker
                OutlinedTextField(
                    value = customMorseText,
                    onValueChange = { customMorseText = it },
                    label = { Text("Custom Text Signal") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = SurvivalOrange
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.blinkMorseText(customMorseText) },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("BLINK CUSTOM MORSE SIGNAL")
                }
            }
            5 -> { // Screen-as-Light White
                Button(
                    onClick = {
                        selectedScreenColor = Color.White
                        isFullLightActive = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("FULL WHITE SCREEN LIGHT", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            6 -> { // Colored Screen Light
                Text("Select Screen Light Color:", color = TextPrimary, fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colors = listOf(Color.White, Color.Red, Color.Green, Color.Blue, Color(0xFFFF6D00), Color(0xFF00E5FF))
                    colors.forEach { col ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(col, CircleShape)
                                .clickable {
                                    selectedScreenColor = col
                                    isFullLightActive = true
                                }
                        )
                    }
                }
            }
            7 -> { // Red Night-Vision Dim Mode
                Button(
                    onClick = {
                        selectedScreenColor = Color(0xFF800000)
                        isFullLightActive = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF800000)),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("ACTIVATE RED NIGHT-VISION MODE", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            8 -> { // Screen Flash + Siren Combo
                Button(
                    onClick = {
                        viewModel.triggerOneTapSos(emptyList())
                        selectedScreenColor = Color.Red
                        isFullLightActive = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("START FLASH + SIREN COMBO", fontWeight = FontWeight.Bold)
                }
            }
            9 -> { // Emergency Beacon
                Text("Beacon Flash Interval: ${beaconIntervalSec.toInt()} Seconds", color = TextPrimary, fontWeight = FontWeight.Bold)
                Slider(
                    value = beaconIntervalSec,
                    onValueChange = { beaconIntervalSec = it },
                    valueRange = 1f..10f,
                    steps = 8
                )
                Button(
                    onClick = {
                        if (isBeaconRunning) {
                            viewModel.stopStrobe()
                            isBeaconRunning = false
                        } else {
                            viewModel.startStrobe(1f / beaconIntervalSec)
                            isBeaconRunning = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isBeaconRunning) EmergencyRed else TacticalAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isBeaconRunning) "STOP BEACON" else "START PERIODIC BEACON")
                }
            }
            10 -> { // Wig-Wag Alternating Flash
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(if (wigWagState > 0.5f) Color.Red else Color.Blue, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("WIG-WAG POLICE FLASH PATTERN", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
            11 -> { // Brightness-Locked Emergency Mode
                Button(
                    onClick = {
                        val activity = context as? Activity
                        val layoutParams = activity?.window?.attributes
                        layoutParams?.screenBrightness = 1.0f
                        activity?.window?.attributes = layoutParams
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LOCK SCREEN TO MAXIMUM BRIGHTNESS (100%)")
                }
            }
            12 -> { // Flashlight Quick Settings & Widget Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("QUICK TILE & WIDGET CONFIG", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("1. Add 'Apocalypse Torch' Quick Settings tile in Android status bar.", color = TextPrimary, fontSize = 12.sp)
                        Text("2. Add '1-Tap SOS' widget to device Home Screen.", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }

        if (isFullLightActive) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(selectedScreenColor)
                    .clickable { isFullLightActive = false },
                color = selectedScreenColor
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("TAP ANYWHERE TO CLOSE LIGHT", color = if (selectedScreenColor == Color.White) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
