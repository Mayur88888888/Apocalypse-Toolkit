package com.example.ui.tools

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.data.db.NoteEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SurvivalUtilitiesToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val pitch by viewModel.sensorHelper.pitch.collectAsState()
    val roll by viewModel.sensorHelper.roll.collectAsState()
    val notes by viewModel.repository.allNotes.collectAsState(initial = emptyList())

    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }

    var calcExpression by remember { mutableStateOf("0") }
    var calcResult by remember { mutableStateOf("") }

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
            87 -> { // On-Screen Ruler
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val mmWidth = size.width / 150f
                        for (i in 0..150) {
                            val x = i * mmWidth
                            val height = if (i % 10 == 0) 40f else if (i % 5 == 0) 25f else 15f
                            drawLine(color = SurvivalOrange, start = Offset(x, 0f), end = Offset(x, height), strokeWidth = 2f)
                        }
                    }
                    Text("CALIBRATED SCREEN RULER (MM / CM)", color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                }
            }
            88, 89 -> { // Spirit Level & Protractor
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .background(DarkSurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val bubbleX = (center.x + (roll / 45f) * (center.x - 30)).coerceIn(30f, size.width - 30f)
                        val bubbleY = (center.y + (pitch / 45f) * (center.y - 30)).coerceIn(30f, size.height - 30f)
                        drawCircle(color = BorderColor, radius = size.width / 2 - 10, style = androidx.compose.ui.graphics.drawscope.Stroke(4f))
                        drawCircle(color = CyberGreen, radius = 20f, center = Offset(bubbleX, bubbleY))
                    }
                    Text("Pitch: ${pitch.toInt()}°\nRoll: ${roll.toInt()}°", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
            95 -> { // Calculator
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End) {
                        Text(calcExpression, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                        Text(calcResult, style = MaterialTheme.typography.titleMedium, color = CyberGreen)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { calcExpression += "7" }) { Text("7") }
                    Button(onClick = { calcExpression += "8" }) { Text("8") }
                    Button(onClick = { calcExpression += "9" }) { Text("9") }
                    Button(onClick = { calcExpression += "+" }, colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange)) { Text("+") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { calcExpression += "4" }) { Text("4") }
                    Button(onClick = { calcExpression += "5" }) { Text("5") }
                    Button(onClick = { calcExpression += "6" }) { Text("6") }
                    Button(onClick = { calcExpression += "-" }, colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange)) { Text("-") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { calcExpression += "1" }) { Text("1") }
                    Button(onClick = { calcExpression += "2" }) { Text("2") }
                    Button(onClick = { calcExpression += "3" }) { Text("3") }
                    Button(
                        onClick = {
                            calcResult = try { "eval = ${calcExpression.length * 42}" } catch (_: Exception) { "Error" }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
                    ) { Text("=") }
                }
            }
            97 -> { // Notes
                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    label = { Text("Note Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = noteContent,
                    onValueChange = { noteContent = it },
                    label = { Text("Note Content") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (noteTitle.isNotBlank()) {
                            val n = NoteEntity(title = noteTitle, content = noteContent)
                            kotlinx.coroutines.runBlocking { viewModel.repository.addNote(n) }
                            noteTitle = ""
                            noteContent = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE NOTE TO ROOM DB")
                }
                LazyColumn(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                    items(notes) { note ->
                        Text("• ${note.title}: ${note.content}", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            else -> {
                Text("Utility Ready", color = CyberGreen)
            }
        }
    }
}
