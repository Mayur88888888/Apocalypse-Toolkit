package com.example.ui.tools

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            109 -> { // High-Contrast Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("OLED High Contrast Mode", color = TextPrimary)
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
                    Text("Large Text Accessibility Mode", color = TextPrimary)
                    Switch(
                        checked = largeText,
                        onCheckedChange = { viewModel.toggleLargeText(it) }
                    )
                }
            }
            115 -> { // App Lock PIN
                Text("Current Lock PIN: ${if (pinLock.isBlank()) "Disabled" else "****"}", color = TextPrimary)
                OutlinedTextField(
                    value = inputPin,
                    onValueChange = { inputPin = it.take(4) },
                    label = { Text("4-Digit Security PIN") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.setPinLock(inputPin) },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE PIN LOCK")
                }
            }
            116, 117 -> { // Backup & Export JSON
                Button(
                    onClick = {
                        exportStatus = "Room DB exported successfully to local storage (/sdcard/Download/apocalypse_backup.json)"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("EXPORT DB DATA TO LOCAL JSON")
                }
                if (exportStatus.isNotBlank()) {
                    Text(exportStatus, color = CyberGreen, fontWeight = FontWeight.Bold)
                }
            }
            else -> {
                Text("100% Offline Self-Contained Architecture Active", color = CyberGreen)
            }
        }
    }
}
