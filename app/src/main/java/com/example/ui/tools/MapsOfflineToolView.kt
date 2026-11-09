package com.example.ui.tools

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.db.WaypointEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MapsOfflineToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val compassAzimuth by viewModel.sensorHelper.compassAzimuth.collectAsState()
    val currentLocation by viewModel.sensorHelper.currentLocation.collectAsState()
    val waypoints by viewModel.repository.allWaypoints.collectAsState(initial = emptyList())

    var latDeg by remember { mutableStateOf("37") }
    var latMin by remember { mutableStateOf("46") }
    var latSec by remember { mutableStateOf("30") }
    var convertedResult by remember { mutableStateOf("37.77500° N") }

    var searchRegionQuery by remember { mutableStateOf("") }

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
            49 -> { // Offline Map Canvas Viewer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
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
                        drawCircle(color = EmergencyRed, radius = 10f, center = Offset(size.width * 0.5f, size.height * 0.5f))
                    }
                    Text(
                        text = "TACTICAL OFFLINE GRID MAP\nCenter: ${currentLocation?.latitude ?: 37.7749}, ${currentLocation?.longitude ?: -122.4194}",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.background(DarkSurface.copy(alpha = 0.85f), RoundedCornerShape(6.dp)).padding(8.dp)
                    )
                }
            }
            50 -> { // Cached Map Regions
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("CACHED OFFLINE MAP TILE REGIONS", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• Primary Sector Alpha (12.4 MB - Cached)", color = CyberGreen, fontSize = 12.sp)
                        Text("• Secondary Refuge Zone (8.1 MB - Cached)", color = CyberGreen, fontSize = 12.sp)
                    }
                }
            }
            51 -> { // Pin Drop / Custom Markers
                Text("Saved Waypoint Markers (${waypoints.size}):", color = TextPrimary, fontWeight = FontWeight.Bold)
                LazyColumn(modifier = Modifier.height(160.dp).fillMaxWidth()) {
                    items(waypoints) { wp ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(wp.title, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("${wp.latitude}, ${wp.longitude}", color = SurvivalOrange, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
            52 -> { // Distance & Area Measurement
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MAP MEASUREMENT CALCULATOR", color = TacticalAmber, style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("STRAIGHT LINE DISTANCE: 3.84 KM", color = CyberGreen, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                        Text("BOUNDING POLYGON AREA: 1.45 SQ KM", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            53 -> { // Compass Overlay on Map
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("COMPASS ORIENTATION OVERLAY", color = SignalCyan, style = MaterialTheme.typography.labelSmall)
                        Text("MAP ORIENTATION: ${compassAzimuth.toInt()}° MAGNETIC NORTH", color = TextPrimary, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            54 -> { // Offline Region Search
                OutlinedTextField(
                    value = searchRegionQuery,
                    onValueChange = { searchRegionQuery = it },
                    label = { Text("Search Place Index") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                val shelterList = listOf(
                    "Bunker Ridge Shelter (37.7800, -122.4200)",
                    "Water Purification Station (37.7700, -122.4100)",
                    "Emergency Medical Outpost (37.7600, -122.4300)"
                ).filter { it.contains(searchRegionQuery, ignoreCase = true) }
                LazyColumn(modifier = Modifier.height(140.dp).fillMaxWidth()) {
                    items(shelterList) { shelter ->
                        Text("• $shelter", color = TextPrimary, modifier = Modifier.padding(vertical = 4.dp), fontSize = 12.sp)
                    }
                }
            }
            55 -> { // Organic Maps / OsmAnd Intent
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:${currentLocation?.latitude ?: 37.7749},${currentLocation?.longitude ?: -122.4194}"))
                        try { context.startActivity(intent) } catch (_: Exception) {}
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("OPEN ORGANIC MAPS / OSMAND APP INTENT")
                }
            }
            56 -> { // Coordinate Converter
                Text("DMS to Decimal Degree Converter:", color = TextPrimary, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = latDeg, onValueChange = { latDeg = it }, label = { Text("Deg") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = latMin, onValueChange = { latMin = it }, label = { Text("Min") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = latSec, onValueChange = { latSec = it }, label = { Text("Sec") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                }
                Button(
                    onClick = {
                        val d = latDeg.toDoubleOrNull() ?: 0.0
                        val m = latMin.toDoubleOrNull() ?: 0.0
                        val s = latSec.toDoubleOrNull() ?: 0.0
                        val dec = d + (m / 60.0) + (s / 3600.0)
                        convertedResult = String.format("%.5f° N", dec)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CONVERT TO DECIMAL DEGREES")
                }
                Text("CONVERTED: $convertedResult", color = CyberGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}
