package com.candyspace.stackoverflowusers.ui.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = StackOrange,
    onPrimary = Color.White,
    secondary = StackBlue,
    onSecondary = Color.White,
    tertiary = StackOrangeLight,
    background = Color(0xFF1C1D1F),
    surface = Color(0xFF1C1D1F),
    primaryContainer = DarkCharcoal,
    onPrimaryContainer = Color.White,
    surfaceVariant = MediumCharcoal,
    onSurfaceVariant = Color(0xFFD6D9DC),
)

private val LightColorScheme = lightColorScheme(
    primary = StackOrange,
    onPrimary = Color.White,
    secondary = StackBlue,
    onSecondary = Color.White,
    tertiary = StackBlueDark,
    background = LightGreyBackground,
    surface = Color.White,
    primaryContainer = LightGreyContainer,
    onPrimaryContainer = DarkCharcoal,
    surfaceVariant = Color(0xFFF1F2F3),
    onSurfaceVariant = DarkCharcoal,
)

@Composable
fun StackAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
