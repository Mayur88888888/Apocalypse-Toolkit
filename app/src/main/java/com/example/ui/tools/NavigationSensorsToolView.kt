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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ToolItem
import com.example.data.db.WaypointEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.utils.AstronomicalMath
import kotlin.math.roundToInt

@Composable
fun NavigationSensorsToolView(
    tool: ToolItem,
    viewModel: MainViewModel
) {
    val compassAzimuth by viewModel.sensorHelper.compassAzimuth.collectAsState()
    val currentLocation by viewModel.sensorHelper.currentLocation.collectAsState()
    val pressure by viewModel.sensorHelper.pressure.collectAsState()
    val ambientTemp by viewModel.sensorHelper.ambientTemp.collectAsState()
    val stepCount by viewModel.sensorHelper.stepCount.collectAsState()
    val waypoints by viewModel.repository.allWaypoints.collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()
    var waypointTitle by remember { mutableStateOf("Waypoint #${waypoints.size + 1}") }

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
            36 -> { // Digital Compass
                val animatedAzimuth by animateFloatAsState(targetValue = -compassAzimuth, label = "compass")
                Box(
                    modifier = Modifier
                        .size(200.dp)
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
                        
                        // North Needle (Red)
                        val northPath = Path().apply {
                            moveTo(center.x, center.y - radius + 20)
                            lineTo(center.x - 15, center.y)
                            lineTo(center.x + 15, center.y)
                            close()
                        }
                        drawPath(northPath, color = EmergencyRed)

                        // South Needle (White)
                        val southPath = Path().apply {
                            moveTo(center.x, center.y + radius - 20)
                            lineTo(center.x - 15, center.y)
                            lineTo(center.x + 15, center.y)
                            close()
                        }
                        drawPath(southPath, color = TextPrimary)
                    }
                    Text(
                        text = "${compassAzimuth.roundToInt()}°",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                        color = TextPrimary
                    )
                }
            }
            37, 38 -> { // GPS Coordinates & Waypoint
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("LATITUDE: ${currentLocation?.latitude ?: "37.7749 (Acquiring...)"}", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("LONGITUDE: ${currentLocation?.longitude ?: "-122.4194 (Acquiring...)"}", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("ALTITUDE: ${currentLocation?.altitude?.roundToInt() ?: 45} m", color = TacticalAmber)
                        Text("ACCURACY: ±${currentLocation?.accuracy?.roundToInt() ?: 5} m", color = CyberGreen)
                    }
                }
                OutlinedTextField(
                    value = waypointTitle,
                    onValueChange = { waypointTitle = it },
                    label = { Text("Waypoint Label") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = SurvivalOrange
                    ),
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
                        kotlinx.coroutines.runBlocking { viewModel.repository.addWaypoint(wp) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurvivalOrange),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE WAYPOINT TO ROOM DB")
                }
            }
            39, 40 -> { // Backtrack & Breadcrumbs
                Text("Saved Waypoints (${waypoints.size}):", color = TextPrimary, fontWeight = FontWeight.Bold)
                LazyColumn(modifier = Modifier.height(180.dp).fillMaxWidth()) {
                    items(waypoints) { wp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(wp.title, color = TextPrimary)
                            Text("${wp.latitude}, ${wp.longitude}", color = TextSecondary, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = BorderColor)
                    }
                }
            }
            41, 42 -> { // Altitude & Barometer Weather
                val baro = pressure ?: 1013.25f
                val weatherForecast = if (baro < 1005) "STORMY / LOW PRESSURE" else if (baro > 1020) "CLEAR / HIGH PRESSURE" else "STABLE / FAIR WEATHER"
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("BAROMETRIC PRESSURE: ${baro.toInt()} hPa", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Text("BAROMETRIC ALTITUDE: ${((1013.25 - baro) * 9).toInt()} m", color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("WEATHER TREND: $weatherForecast", color = CyberGreen, fontWeight = FontWeight.Black)
                    }
                }
            }
            44 -> { // Speedometer
                val speedKmh = ((currentLocation?.speed ?: 0f) * 3.6f).roundToInt()
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .background(DarkSurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$speedKmh", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black), color = SurvivalOrange)
                        Text(text = "KM/H", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
            45 -> { // Step Counter
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("STEP COUNT", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                        Text("$stepCount STEPS", color = CyberGreen, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                        Text("ESTIMATED DISTANCE: ${(stepCount * 0.75 / 1000.0).let { String.format("%.2f km", it) }}", color = TextPrimary)
                    }
                }
            }
            46, 47, 48 -> { // Sun, Moon, Stars
                val sunTimes = AstronomicalMath.calculateSunriseSunset(currentLocation?.latitude ?: 37.7749, currentLocation?.longitude ?: -122.4194)
                val moonInfo = AstronomicalMath.getMoonPhase()
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SOLAR TIMES", color = TacticalAmber, fontWeight = FontWeight.Bold)
                        Text("Sunrise: ${sunTimes.first} AM | Sunset: ${sunTimes.second} PM", color = TextPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("LUNAR PHASE", color = SignalCyan, fontWeight = FontWeight.Bold)
                        Text("Phase: ${moonInfo.name} (${moonInfo.illuminationPercent}% Illuminated)", color = TextPrimary)
                    }
                }
            }
            else -> {
                Text("Sensor Reading: Active", color = CyberGreen)
            }
        }
    }
}
