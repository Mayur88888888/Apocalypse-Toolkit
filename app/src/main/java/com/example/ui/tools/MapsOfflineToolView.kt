package com.example.ui.tools

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MapsOfflineToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var latDeg by remember { mutableStateOf("37") }
    var latMin by remember { mutableStateOf("46") }
    var latSec by remember { mutableStateOf("30") }
    var convertedDec by remember { mutableStateOf("37.7750° N") }

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
            49, 50, 51, 52, 53 -> { // Canvas Map Viewer & Pin Markers
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(DarkSurfaceVariant, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridStep = 40f
                        var x = 0f
                        while (x < size.width) {
                            drawLine(color = BorderColor.copy(alpha = 0.4f), start = Offset(x, 0f), end = Offset(x, size.height))
                            x += gridStep
                        }
                        var y = 0f
                        while (y < size.height) {
                            drawLine(color = BorderColor.copy(alpha = 0.4f), start = Offset(0f, y), end = Offset(size.width, y))
                            y += gridStep
                        }
                        // Pin 1
                        drawCircle(color = SurvivalOrange, radius = 12f, center = Offset(size.width * 0.4f, size.height * 0.4f))
                        // Pin 2
                        drawCircle(color = CyberGreen, radius = 12f, center = Offset(size.width * 0.7f, size.height * 0.6f))
                        // Distance line
                        drawLine(
                            color = TacticalAmber,
                            start = Offset(size.width * 0.4f, size.height * 0.4f),
                            end = Offset(size.width * 0.7f, size.height * 0.6f),
                            strokeWidth = 3f
                        )
                    }
                    Text(
                        text = "TACTICAL GRID MAP CANVAS\n[Distance: 1.42 km]",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .background(DarkSurface.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    )
                }
            }
            55 -> { // Organic Maps / OsmAnd Intent
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:37.7749,-122.4194"))
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OPEN ORGANIC MAPS / OSMAND INTENT")
                }
            }
            56 -> { // Coordinate Converter (DMS <-> Dec)
                Text("DMS to Decimal Converter:", color = TextPrimary, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = latDeg,
                        onValueChange = { latDeg = it },
                        label = { Text("Deg") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = latMin,
                        onValueChange = { latMin = it },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = latSec,
                        onValueChange = { latSec = it },
                        label = { Text("Sec") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
                Button(
                    onClick = {
                        val d = latDeg.toDoubleOrNull() ?: 0.0
                        val m = latMin.toDoubleOrNull() ?: 0.0
                        val s = latSec.toDoubleOrNull() ?: 0.0
                        val dec = d + (m / 60.0) + (s / 3600.0)
                        convertedDec = String.format("%.5f° N", dec)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CONVERT TO DECIMAL")
                }
                Text("RESULT: $convertedDec", color = CyberGreen, fontWeight = FontWeight.Bold)
            }
            else -> {
                Text("Offline Region Search Ready", color = CyberGreen)
            }
        }
    }
}
