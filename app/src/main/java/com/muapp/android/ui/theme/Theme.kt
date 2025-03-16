package com.muapp.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.muapp.android.ui.theme.*

// Premium Light Colors
private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = SecondaryDark,
    tertiary = Accent,
    background = Color.White,
    onBackground = DarkGray,
    surface = Color.White,
    onSurface = DarkGray,
    error = Error,
    onError = Color.White
)

// Premium Dark Colors
private val DarkColors = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = PrimaryDark,
    primaryContainer = Primary,
    onPrimaryContainer = Color.White,
    secondary = SecondaryLight,
    onSecondary = SecondaryDark,
    secondaryContainer = Secondary,
    onSecondaryContainer = Color.White,
    tertiary = Accent,
    background = Background,
    onBackground = LightGray,
    surface = Surface,
    onSurface = Color.White,
    error = Error,
    onError = Color.White
)

// Define Custom Typography
private val AppTypography = Typography()

// Define Custom Shapes
private val AppShapes = Shapes()

@Composable
fun MUAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}