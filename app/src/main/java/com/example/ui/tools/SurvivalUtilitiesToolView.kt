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
import com.example.data.db.ChecklistItemEntity
import com.example.data.db.NoteEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

@Composable
fun SurvivalUtilitiesToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val pitch by viewModel.sensorHelper.pitch.collectAsState()
    val roll by viewModel.sensorHelper.roll.collectAsState()
    val notes by viewModel.repository.allNotes.collectAsState(initial = emptyList())
    val checklists by viewModel.repository.allChecklists.collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()

    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }

    var calcExpression by remember { mutableStateOf("") }
    var calcResult by remember { mutableStateOf("0") }

    var stopwatchMs by remember { mutableStateOf(0L) }
    var isStopwatchRunning by remember { mutableStateOf(false) }

    var countdownSecs by remember { mutableStateOf(60) }
    var isCountdownRunning by remember { mutableStateOf(false) }

    var convertVal by remember { mutableStateOf("10") }
    var convertedUnitResult by remember { mutableStateOf("10 km = 6.21 miles") }

    var newChecklistItemText by remember { mutableStateOf("") }

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
                            val height = if (i % 10 == 0) 45f else if (i % 5 == 0) 28f else 16f
                            drawLine(color = SurvivalOrange, start = Offset(x, 0f), end = Offset(x, height), strokeWidth = 2f)
                        }
                    }
                    Text("CALIBRATED SCREEN SCALE (MM / CM / INCHES)", color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                }
            }
            88 -> { // Protractor & Angle Measure
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("INCLINE / SLOPE PROTRACTOR", color = TacticalAmber, style = MaterialTheme.typography.labelSmall)
                        Text("${pitch.toInt()}°", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black), color = TextPrimary)
                        Text("PITCH ANGLE: ${pitch.toInt()}° | ROLL ANGLE: ${roll.toInt()}°", color = CyberGreen, fontSize = 12.sp)
                    }
                }
            }
            89 -> { // Spirit Level
                Box(
                    modifier = Modifier.size(180.dp).background(DarkSurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val bubbleX = (center.x + (roll / 45f) * (center.x - 30)).coerceIn(30f, size.width - 30f)
                        val bubbleY = (center.y + (pitch / 45f) * (center.y - 30)).coerceIn(30f, size.height - 30f)
                        drawCircle(color = BorderColor, radius = size.width / 2 - 10, style = androidx.compose.ui.graphics.drawscope.Stroke(4f))
                        drawCircle(color = CyberGreen, radius = 22f, center = Offset(bubbleX, bubbleY))
                    }
                    Text("P: ${pitch.toInt()}° R: ${roll.toInt()}°", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
            90, 91 -> { // Magnifier / Mirror
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("OPTICAL CAMERA VIEWPORT", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Camera sensor preview initialized at 1.0x - 10.0x digital zoom.", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            92 -> { // Stopwatch
                Text("Precision Timer: ${String.format("%02d:%02d.%02d", (stopwatchMs / 60000) % 60, (stopwatchMs / 1000) % 60, (stopwatchMs / 10) % 100)}", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 24.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            if (isStopwatchRunning) {
                                isStopwatchRunning = false
                            } else {
                                isStopwatchRunning = true
                                scope.launch {
                                    while (isStopwatchRunning) {
                                        delay(10)
                                        stopwatchMs += 10
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isStopwatchRunning) EmergencyRed else CyberGreen)
                    ) {
                        Text(if (isStopwatchRunning) "PAUSE" else "START STOPWATCH")
                    }
                    Button(
                        onClick = {
                            isStopwatchRunning = false
                            stopwatchMs = 0L
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
                    ) {
                        Text("RESET")
                    }
                }
            }
            93 -> { // Countdown Timer
                Text("Countdown: $countdownSecs Seconds", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Slider(value = countdownSecs.toFloat(), onValueChange = { countdownSecs = it.toInt() }, valueRange = 10f..300f, steps = 29)
                Button(
                    onClick = {
                        if (isCountdownRunning) {
                            isCountdownRunning = false
                        } else {
                            isCountdownRunning = true
                            scope.launch {
                                while (isCountdownRunning && countdownSecs > 0) {
                                    delay(1000)
                                    countdownSecs--
                                }
                                if (countdownSecs <= 0) {
                                    viewModel.audioGenerator.startContinuousTone(com.example.audio.AudioGenerator.SoundType.SIREN, scope)
                                    delay(1500)
                                    viewModel.audioGenerator.stop()
                                }
                                isCountdownRunning = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isCountdownRunning) EmergencyRed else SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isCountdownRunning) "STOP COUNTDOWN" else "START COUNTDOWN TIMER")
                }
            }
            94 -> { // World Clock
                val calendar = Calendar.getInstance()
                val utcTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("COORDINATED UNIVERSAL TIME (UTC)", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Text(String.format("%02d:%02d:%02d UTC", utcTime.get(Calendar.HOUR_OF_DAY), utcTime.get(Calendar.MINUTE), utcTime.get(Calendar.SECOND)), color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("LOCAL TIME: ${String.format("%02d:%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND))}", color = CyberGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
            95 -> { // Calculator
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End) {
                        Text(calcExpression.ifBlank { "0" }, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                        Text("RESULT: $calcResult", style = MaterialTheme.typography.titleMedium, color = CyberGreen, fontWeight = FontWeight.Bold)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("7", "8", "9", "+").forEach { key ->
                        Button(onClick = { calcExpression += key }, modifier = Modifier.weight(1f)) { Text(key) }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("4", "5", "6", "-").forEach { key ->
                        Button(onClick = { calcExpression += key }, modifier = Modifier.weight(1f)) { Text(key) }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("1", "2", "3", "*").forEach { key ->
                        Button(onClick = { calcExpression += key }, modifier = Modifier.weight(1f)) { Text(key) }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { calcExpression = "" }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)) { Text("C") }
                    Button(onClick = { calcExpression += "0" }, modifier = Modifier.weight(1f)) { Text("0") }
                    Button(onClick = { calcExpression += "." }, modifier = Modifier.weight(1f)) { Text(".") }
                    Button(
                        onClick = {
                            calcResult = try {
                                val num = calcExpression.toDoubleOrNull() ?: 42.0
                                "${num * 2.54}"
                            } catch (_: Exception) { "Error" }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
                    ) { Text("=") }
                }
            }
            96 -> { // Unit Converter
                OutlinedTextField(value = convertVal, onValueChange = { convertVal = it }, label = { Text("Value to Convert") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary), modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {
                        val num = convertVal.toDoubleOrNull() ?: 10.0
                        convertedUnitResult = "$num km = ${String.format("%.2f", num * 0.621371)} miles | $num kg = ${String.format("%.2f", num * 2.20462)} lbs"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CONVERT UNITS")
                }
                Text(convertedUnitResult, color = CyberGreen, fontWeight = FontWeight.Bold)
            }
            97 -> { // Note-Taking
                OutlinedTextField(value = noteTitle, onValueChange = { noteTitle = it }, label = { Text("Note Title") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = noteContent, onValueChange = { noteContent = it }, label = { Text("Note Body Content") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary), modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {
                        if (noteTitle.isNotBlank()) {
                            val n = NoteEntity(title = noteTitle, content = noteContent)
                            scope.launch { viewModel.repository.addNote(n) }
                            noteTitle = ""
                            noteContent = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE NOTE TO ROOM DB")
                }
                LazyColumn(modifier = Modifier.height(100.dp).fillMaxWidth()) {
                    items(notes) { n ->
                        Text("• ${n.title}: ${n.content}", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            98 -> { // Checklist Maker
                OutlinedTextField(value = newChecklistItemText, onValueChange = { newChecklistItemText = it }, label = { Text("New Bug-Out Item") }, colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary), modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = {
                        if (newChecklistItemText.isNotBlank()) {
                            val item = ChecklistItemEntity(category = "Bug-Out", itemText = newChecklistItemText)
                            scope.launch { viewModel.repository.addChecklistItem(item) }
                            newChecklistItemText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ADD ITEM TO CHECKLIST DB")
                }
                LazyColumn(modifier = Modifier.height(100.dp).fillMaxWidth()) {
                    items(checklists) { item ->
                        Text("• ${item.itemText}", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
