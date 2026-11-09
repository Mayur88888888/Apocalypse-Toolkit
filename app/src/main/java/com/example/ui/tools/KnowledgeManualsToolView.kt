package com.example.ui.tools

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.data.knowledge.KnowledgeData
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun KnowledgeManualsToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val manual = remember(tool.id) {
        val index = (tool.id - 57).coerceIn(0, KnowledgeData.manuals.size - 1)
        KnowledgeData.manuals[index]
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = manual.title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = SurvivalOrange
        )
        Text(
            text = manual.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        if (manual.warnings.isNotBlank()) {
            Surface(
                color = EmergencyRed.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "WARNING: ${manual.warnings}",
                    color = EmergencyRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Text(
            text = "STEP-BY-STEP SURVIVAL MANUAL:",
            style = MaterialTheme.typography.labelLarge,
            color = TacticalAmber
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            items(manual.steps) { step ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                ) {
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
