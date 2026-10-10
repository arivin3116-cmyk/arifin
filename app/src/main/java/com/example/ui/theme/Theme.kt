package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SuperBrainColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1E1500),
    primaryContainer = Color(0xFF3D2E00),
    onPrimaryContainer = GoldLight,
    secondary = EmeraldProfit,
    onSecondary = Color(0xFF002111),
    secondaryContainer = Color(0xFF003920),
    onSecondaryContainer = Color(0xFF86F8B6),
    tertiary = CyanAccent,
    onTertiary = Color(0xFF002022),
    background = ObsidianBackground,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianBorder,
    error = RubyLoss,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = SuperBrainColorScheme,
        typography = Typography,
        content = content
    )
}
