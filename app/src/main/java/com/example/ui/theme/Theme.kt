package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SurvivalDarkColorScheme = darkColorScheme(
    primary = SurvivalOrange,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF3E1F00),
    onPrimaryContainer = SurvivalOrange,
    secondary = TacticalAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3B2E00),
    onSecondaryContainer = TacticalAmber,
    tertiary = CyberGreen,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = EmergencyRed,
    onError = Color.White,
    outline = BorderColor
)

@Composable
fun ApocalypseToolkitTheme(
    darkTheme: Boolean = true, // Dark theme default for survival
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (highContrast) {
        SurvivalDarkColorScheme.copy(
            background = Color.Black,
            surface = Color(0xFF0D0D0D),
            onBackground = Color.White,
            onSurface = Color.White,
            primary = Color(0xFFFF8F00)
        )
    } else {
        SurvivalDarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
