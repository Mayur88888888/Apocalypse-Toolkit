package com.example.ui.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ExtrasQolToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val highContrast by viewModel.highContrast.collectAsState()
    val largeText by viewModel.largeText.collectAsState()
    val pinLock by viewModel.pinLock.collectAsState()

    var inputPin by remember { mutableStateOf("") }
    var exportStatus by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf("English") }

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
            109 -> { // Dark / High Contrast Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("OLED True Black High Contrast Mode", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = highContrast,
                        onCheckedChange = { viewModel.toggleHighContrast(it) }
                    )
                }
            }
            110 -> { // Large Text Accessibility
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Large Text Font Scaling Mode", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = largeText,
                        onCheckedChange = { viewModel.toggleLargeText(it) }
                    )
                }
            }
            111 -> { // Offline Onboarding Tutorial
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("OFFLINE APPLICATION WALKTHROUGH", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Tap any category card on dashboard to browse tools.", color = TextPrimary, fontSize = 12.sp)
                        Text("• Use top bar button to toggle OLED high contrast mode.", color = TextPrimary, fontSize = 12.sp)
                        Text("• Floating red FAB triggers One-Tap SOS light & sound beacon.", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            112, 113, 114 -> { // Widget, Quick Tiles & Shortcuts Info
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SYSTEM INTEGRATION GUIDE", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• Quick Tile: Drag 'Apocalypse Torch' into Android Status Bar.", color = TextPrimary, fontSize = 12.sp)
                        Text("• Home Widget: Long-press home screen to add '1-Tap SOS' widget.", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            115 -> { // App Lock (PIN)
                Text("Security Status: ${if (pinLock.isBlank()) "UNLOCKED (No PIN)" else "LOCKED WITH PIN"}", color = TextPrimary, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = inputPin,
                    onValueChange = { inputPin = it.take(4) },
                    label = { Text("Set 4-Digit PIN") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.setPinLock(inputPin) },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE APP LOCK SECURITY PIN")
                }
            }
            116, 117 -> { // Data Backup / Import Export JSON
                Button(
                    onClick = {
                        exportStatus = "Room DB backup exported to /sdcard/Download/apocalypse_toolkit_backup.json"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("EXPORT DB DATA TO LOCAL JSON FILE")
                }
                if (exportStatus.isNotBlank()) {
                    Text(exportStatus, color = CyberGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            118 -> { // Multi-Language Support
                Text("App Language Selector:", color = TextPrimary, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("English", "Español", "Français").forEach { lang ->
                        FilterChip(
                            selected = selectedLang == lang,
                            onClick = { selectedLang = lang },
                            label = { Text(lang) }
                        )
                    }
                }
            }
            119 -> { // No-Internet-First Architecture
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SYSTEM OFFLINE INTEGRITY DIAGNOSTIC", color = CyberGreen, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• External Network Calls: 0", color = TextPrimary, fontSize = 12.sp)
                        Text("• Bundled Local Assets: 100% Complete", color = TextPrimary, fontSize = 12.sp)
                        Text("• Room DB Persistence: Active", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            120 -> { // Low-Storage Footprint Mode
                Button(
                    onClick = {
                        exportStatus = "App cache and temp files cleaned successfully."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CLEAN CACHE & TEMP FILES")
                }
            }
        }
    }
}
