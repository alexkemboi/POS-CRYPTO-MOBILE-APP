package com.pos.crypto_pay_kt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pos.crypto_pay_kt.domain.model.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = SoftWhite,
    onPrimary = Ink,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = TealLight,
    onSecondary = Color(0xFF002117),
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = Color(0xFFFFB870),
    tertiaryContainer = Color(0xFF6A3B00),
    background = DarkBackground,
    surface = DarkSurface,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurfaceMuted,
    surfaceContainerHigh = DarkSurfaceMuted,
    surfaceContainerHighest = DarkSurfaceStrong,
    onBackground = SoftWhite,
    onSurface = SoftWhite,
    onSurfaceVariant = Color(0xFFB8B8BD),
    outline = Color(0xFF8C8C91),
    outlineVariant = Color(0xFF3B3B40),
    error = Error,
)

private val LightColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = Color.White,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = Warning,
    tertiaryContainer = WarningContainer,
    background = LightBackground,
    surface = LightSurface,
    surfaceContainerLowest = LightSurface,
    surfaceContainerLow = LightSurface,
    surfaceContainer = LightSurfaceMuted,
    surfaceContainerHigh = LightSurfaceMuted,
    surfaceContainerHighest = LightSurfaceStrong,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = Color(0xFF606064),
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = Error,
)

@Composable
fun CryptoPayTheme(
    appTheme: AppTheme = AppTheme.LIGHT,
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (appTheme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
