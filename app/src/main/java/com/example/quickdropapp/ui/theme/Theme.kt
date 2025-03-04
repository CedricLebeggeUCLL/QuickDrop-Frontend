package com.example.quickdropapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = GreenSustainable,
    secondary = EarthBrown,
    tertiary = SkyBlue,
    background = SandBeige,
    surface = WhiteSmoke,
    onPrimary = WhiteSmoke,
    onSecondary = WhiteSmoke,
    onTertiary = Charcoal,
    onBackground = Charcoal,
    onSurface = Charcoal
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkGreen,
    secondary = EarthBrown,
    tertiary = SkyBlue,
    background = Charcoal,
    surface = DarkGreen,
    onPrimary = WhiteSmoke,
    onSecondary = WhiteSmoke,
    onTertiary = WhiteSmoke,
    onBackground = WhiteSmoke,
    onSurface = WhiteSmoke
)

@Composable
fun QuickDropAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}