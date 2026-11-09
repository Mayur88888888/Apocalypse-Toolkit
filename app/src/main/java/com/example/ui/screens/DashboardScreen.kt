package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CategoryCatalog
import com.example.data.ToolCategory
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onCategoryClick: (Int) -> Unit,
    onSosClick: () -> Unit
) {
    val compassAzimuth by viewModel.sensorHelper.compassAzimuth.collectAsState()
    val currentLocation by viewModel.sensorHelper.currentLocation.collectAsState()
    val highContrast by viewModel.highContrast.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "APOCALYPSE TOOLKIT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            ),
                            color = SurvivalOrange
                        )
                        Text(
                            text = "100% OFFLINE • 150 TOOLS",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleHighContrast(!highContrast) },
                        modifier = Modifier.testTag("high_contrast_toggle")
                    ) {
                        Icon(
                            imageVector = if (highContrast) Icons.Default.Contrast else Icons.Default.Brightness6,
                            contentDescription = "Toggle High Contrast",
                            tint = TacticalAmber
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onSosClick,
                containerColor = EmergencyRed,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Warning, contentDescription = "SOS") },
                text = { Text("ONE-TAP SOS", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("dashboard_sos_fab")
            )
        },
        containerColor = if (highContrast) Color.Black else DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Status Bar Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Compass Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Compass",
                            tint = CyberGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${compassAzimuth.toInt()}° N",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    // GPS Fix Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "GPS",
                            tint = if (currentLocation != null) CyberGreen else TacticalAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentLocation != null) "GPS READY" else "ACQUIRING...",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (currentLocation != null) CyberGreen else TacticalAmber
                        )
                    }

                    // Offline Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = "Offline",
                            tint = SignalCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFLINE",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = SignalCyan
                        )
                    }
                }
            }

            Text(
                text = "SURVIVAL CATEGORIES",
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 12 Category Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(CategoryCatalog.categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: ToolCategory,
    onClick: () -> Unit
) {
    val iconVector = getCategoryIcon(category.iconName)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() }
            .testTag("category_card_${category.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = SurvivalOrange.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = category.title,
                        tint = SurvivalOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TacticalAmber.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${category.toolCount} Tools",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TacticalAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    fontSize = 11.sp
                )
            }
        }
    }
}

fun getCategoryIcon(name: String): ImageVector {
    return when (name) {
        "FlashlightOn" -> Icons.Default.FlashOn
        "VolumeUp" -> Icons.Default.VolumeUp
        "Radio" -> Icons.Default.Radio
        "Explore" -> Icons.Default.Explore
        "Map" -> Icons.Default.Map
        "MenuBook" -> Icons.Default.MenuBook
        "MedicalServices" -> Icons.Default.MedicalServices
        "BatteryChargingFull" -> Icons.Default.BatteryChargingFull
        "Build" -> Icons.Default.Build
        "Warning" -> Icons.Default.Warning
        "Settings" -> Icons.Default.Settings
        "Contactless" -> Icons.Default.CellTower
        else -> Icons.Default.Shield
    }
}
