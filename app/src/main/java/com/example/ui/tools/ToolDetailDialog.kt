package com.example.ui.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CategoryCatalog
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ToolDetailDialog(
    toolId: Int,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val tool = CategoryCatalog.tools.find { it.id == toolId } ?: return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .testTag("tool_dialog_surface"),
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header with Close Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOOL #${tool.id}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TacticalAmber
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_tool_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Tool",
                            tint = TextPrimary
                        )
                    }
                }

                HorizontalDivider(color = BorderColor)

                // Tool Content View depending on Category
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 16.dp)
                ) {
                    when (tool.categoryId) {
                        1 -> LightSignalingToolView(tool = tool, viewModel = viewModel)
                        2 -> AudioAlertsToolView(tool = tool, viewModel = viewModel)
                        3 -> RadioBroadcastToolView(tool = tool, viewModel = viewModel)
                        4 -> NavigationSensorsToolView(tool = tool, viewModel = viewModel)
                        5 -> MapsOfflineToolView(tool = tool, viewModel = viewModel)
                        6 -> KnowledgeManualsToolView(tool = tool, viewModel = viewModel)
                        7 -> HealthSafetyToolView(tool = tool, viewModel = viewModel)
                        8 -> PowerBatteryToolView(tool = tool, viewModel = viewModel)
                        9 -> SurvivalUtilitiesToolView(tool = tool, viewModel = viewModel)
                        10 -> EmergencySosToolView(tool = tool, viewModel = viewModel)
                        11 -> ExtrasQolToolView(tool = tool, viewModel = viewModel)
                        12 -> CommsToolView(tool = tool, viewModel = viewModel)
                        else -> LightSignalingToolView(tool = tool, viewModel = viewModel)
                    }
                }
            }
        }
    }
}
