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
    secondary = NavyLight,
    background = DarkBackground,
    surface = DarkSurface,
    error = Error,
)

private val LightColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,
    secondary = Teal,
    background = LightBackground,
    surface = LightSurface,
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
