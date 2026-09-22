package com.pos.crypto_pay_kt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.pos.crypto_pay_kt.domain.model.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = TealLight,
    onPrimary = Color(0xFF003731),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = NavyLight,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = Color(0xFFFFB870),
    tertiaryContainer = Color(0xFF6A3B00),
    background = DarkBackground,
    surface = DarkSurface,
    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF44474F),
    error = Error,
)

private val LightColorScheme = lightColorScheme(
    primary = Navy,
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
