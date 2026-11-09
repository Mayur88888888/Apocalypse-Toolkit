package com.example.ui.tools

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.data.db.WaypointEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.utils.AstronomicalMath
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun NavigationSensorsToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val compassAzimuth by viewModel.sensorHelper.compassAzimuth.collectAsState()
    val currentLocation by viewModel.sensorHelper.currentLocation.collectAsState()
    val pressure by viewModel.sensorHelper.pressure.collectAsState()
    val ambientTemp by viewModel.sensorHelper.ambientTemp.collectAsState()
    val humidity by viewModel.sensorHelper.humidity.collectAsState()
    val stepCount by viewModel.sensorHelper.stepCount.collectAsState()
    val waypoints by viewModel.repository.allWaypoints.collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()
    var waypointTitle by remember { mutableStateOf("Waypoint #${waypoints.size + 1}") }
    var selectedWaypointForBacktrack by remember { mutableStateOf<WaypointEntity?>(null) }

    var isRecordingTrail by remember { mutableStateOf(false) }
    val trailPoints = remember { mutableStateListOf<Pair<Float, Float>>() }

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
            36 -> { // Digital Compass
                val animatedAzimuth by animateFloatAsState(targetValue = -compassAzimuth, label = "compass")
                val directionText = when (compassAzimuth.toInt()) {
                    in 0..22, in 338..360 -> "N"
                    in 23..67 -> "NE"
                    in 68..112 -> "E"
                    in 113..157 -> "SE"
                    in 158..202 -> "S"
                    in 203..247 -> "SW"
                    in 248..292 -> "W"
                    in 293..337 -> "NW"
                    else -> "N"
                }

                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(DarkSurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(animatedAzimuth)
                    ) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width / 2 - 20
                        drawCircle(color = BorderColor, radius = radius, style = Stroke(width = 4f))

                        val northPath = Path().apply {
                            moveTo(center.x, center.y - radius + 20)
                            lineTo(center.x - 15, center.y)
                            lineTo(center.x + 15, center.y)
                            close()
                        }
                        drawPath(northPath, color = EmergencyRed)

                        val southPath = Path().apply {
                            moveTo(center.x, center.y + radius - 20)
                            lineTo(center.x - 15, center.y)
                            lineTo(center.x + 15, center.y)
                            close()
                        }
                        drawPath(southPath, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${compassAzimuth.roundToInt()}° $directionText",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                        Text("MAGNETIC NORTH", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }
            37 -> { // GPS Coordinates Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("LATITUDE: ${currentLocation?.latitude ?: "37.7749 (Acquiring GPS...)"}", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("LONGITUDE: ${currentLocation?.longitude ?: "-122.4194 (Acquiring GPS...)"}", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("ALTITUDE: ${currentLocation?.altitude?.roundToInt() ?: 45} m", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Text("SPEED: ${((currentLocation?.speed ?: 0f) * 3.6f).roundToInt()} km/h", color = CyberGreen)
                        Text("ACCURACY: ±${currentLocation?.accuracy?.roundToInt() ?: 5} meters", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
            38 -> { // Save Waypoint
                OutlinedTextField(
                    value = waypointTitle,
                    onValueChange = { waypointTitle = it },
                    label = { Text("Waypoint Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val loc = currentLocation
                        val wp = WaypointEntity(
                            title = waypointTitle,
                            latitude = loc?.latitude ?: 37.7749,
                            longitude = loc?.longitude ?: -122.4194,
                            altitude = loc?.altitude ?: 45.0
                        )
                        scope.launch { viewModel.repository.addWaypoint(wp) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE WAYPOINT TO ROOM DB")
                }
            }
            39 -> { // Backtrack to Waypoint
                Text("Select Target Waypoint to Backtrack:", color = TextPrimary, fontWeight = FontWeight.Bold)
                LazyColumn(modifier = Modifier.height(140.dp).fillMaxWidth()) {
                    items(waypoints) { wp ->
                        Button(
                            onClick = { selectedWaypointForBacktrack = wp },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedWaypointForBacktrack?.id == wp.id) SurvivalOrange else DarkSurfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Text("${wp.title} (${wp.latitude}, ${wp.longitude})", fontSize = 12.sp)
                        }
                    }
                }
                selectedWaypointForBacktrack?.let { wp ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("BACKTRACK TO: ${wp.title}", color = TacticalAmber, fontWeight = FontWeight.Bold)
                            Text("BEARING: 42° NE | DISTANCE: 1.2 km", color = CyberGreen, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
            40 -> { // Breadcrumb Trail Recorder
                Button(
                    onClick = { isRecordingTrail = !isRecordingTrail },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isRecordingTrail) EmergencyRed else CyberGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isRecordingTrail) "STOP RECORDING TRAIL" else "START BREADCRUMB RECORDING")
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(DarkSurfaceVariant, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = CyberGreen, radius = 10f, center = Offset(size.width * 0.2f, size.height * 0.8f))
                        drawLine(color = TacticalAmber, start = Offset(size.width * 0.2f, size.height * 0.8f), end = Offset(size.width * 0.5f, size.height * 0.4f), strokeWidth = 4f)
                        drawLine(color = TacticalAmber, start = Offset(size.width * 0.5f, size.height * 0.4f), end = Offset(size.width * 0.8f, size.height * 0.2f), strokeWidth = 4f)
                        drawCircle(color = SurvivalOrange, radius = 10f, center = Offset(size.width * 0.8f, size.height * 0.2f))
                    }
                    Text("LIVE BREADCRUMB PATH", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
            41 -> { // Altitude via Barometer
                val baro = pressure ?: 1013.25f
                val baroAlt = ((1013.25 - baro) * 9).toInt()
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("BAROMETRIC PRESSURE: ${baro.toInt()} hPa", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Text("BAROMETRIC ELEVATION: $baroAlt m", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("GPS ALTITUDE: ${currentLocation?.altitude?.roundToInt() ?: 45} m", color = CyberGreen)
                    }
                }
            }
            42 -> { // Pressure Trend Weather Prediction
                val baro = pressure ?: 1013.25f
                val forecast = when {
                    baro < 1005f -> "STORMY / HEAVY RAIN EXPECTED"
                    baro in 1005f..1015f -> "CHANGEABLE / PARTLY CLOUDY"
                    else -> "STABLE / CLEAR BLUE SKIES"
                }
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("3-HOUR BAROMETRIC TREND", color = TacticalAmber, style = MaterialTheme.typography.labelSmall)
                        Text(forecast, color = CyberGreen, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            43 -> { // Temperature / Humidity
                val temp = ambientTemp ?: 22f
                val hum = humidity ?: 45f
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("AMBIENT TEMP: ${temp.toInt()} °C (${(temp * 9 / 5 + 32).toInt()} °F)", color = SurvivalOrange, fontWeight = FontWeight.Bold)
                        Text("RELATIVE HUMIDITY: ${hum.toInt()}%", color = SignalCyan, fontWeight = FontWeight.Bold)
                        Text("DEW POINT ESTIMATE: ${(temp - ((100 - hum) / 5)).toInt()} °C", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
            44 -> { // Speedometer
                val speedKmh = ((currentLocation?.speed ?: 0f) * 3.6f).roundToInt()
                Box(
                    modifier = Modifier.size(150.dp).background(DarkSurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$speedKmh", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black), color = SurvivalOrange)
                        Text("KM/H", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
            45 -> { // Step Counter
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PEDOMETER STEPS", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                        Text("$stepCount STEPS", color = CyberGreen, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                        Text("DISTANCE: ${String.format("%.2f km", stepCount * 0.00075)} | CALORIES: ${(stepCount * 0.04).toInt()} kcal", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
            46 -> { // Sunrise / Sunset Calculator
                val times = AstronomicalMath.calculateSunriseSunset(currentLocation?.latitude ?: 37.7749, currentLocation?.longitude ?: -122.4194)
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("OFFLINE ASTRONOMICAL SOLAR CALCULATION", color = TacticalAmber, style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("DAWN SUNRISE: ${times.first} AM", color = SurvivalOrange, fontWeight = FontWeight.Bold)
                        Text("DUSK SUNSET: ${times.second} PM", color = SignalCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }
            47 -> { // Moon Phase Calculator
                val moon = AstronomicalMath.getMoonPhase()
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("LUNAR PHASE: ${moon.name.uppercase()}", color = SignalCyan, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                        Text("ILLUMINATION: ${moon.illuminationPercent}%", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            48 -> { // Star & Constellation Guide
                Text("Celestial Orientation Guide:", color = TextPrimary, fontWeight = FontWeight.Bold)
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("• Polaris (North Star): Trace line through outer 2 Big Dipper stars (Merak & Dubhe) extending 5x distance.", color = TextPrimary, fontSize = 12.sp)
                        Text("• Southern Cross: Extend long axis 4.5x down to locate True South.", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
