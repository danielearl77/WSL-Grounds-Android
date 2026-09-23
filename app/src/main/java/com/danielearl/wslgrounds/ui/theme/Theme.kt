package com.danielearl.wslgrounds.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WslBlue = Color(0xFF007AFF)

private val LightColors = lightColorScheme(
    primary = WslBlue,
    secondary = WslBlue,
)

private val DarkColors = darkColorScheme(
    primary = WslBlue,
    secondary = WslBlue,
)

@Composable
fun WSLGroundsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
