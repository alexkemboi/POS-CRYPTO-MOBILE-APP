package com.pos.crypto_pay_kt.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pos.crypto_pay_kt.R
import com.pos.crypto_pay_kt.domain.model.AppLanguage
import com.pos.crypto_pay_kt.domain.model.AppTheme
import com.pos.crypto_pay_kt.presentation.components.CryptoPaySectionHeader
import com.pos.crypto_pay_kt.presentation.components.CryptoPayTopBar

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onBack = onBack,
        onThemeSelected = viewModel::selectTheme,
        onLanguageSelected = { language ->
            viewModel.selectLanguage(language)
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(language.languageTag),
            )
        },
        onLogout = viewModel::logout,
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onThemeSelected: (AppTheme) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onLogout: () -> Unit,
) {
    var showLogoutConfirmation by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CryptoPayTopBar(
                title = stringResource(R.string.settings),
                onBack = onBack,
                onLogout = { showLogoutConfirmation = true },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                CryptoPaySectionHeader(
                    title = stringResource(R.string.language),
                    supportingText = stringResource(R.string.settings_language_hint),
                    modifier = Modifier.padding(bottom = 2.dp),
                )
            }
            items(AppLanguage.entries, key = AppLanguage::languageTag) { language ->
                SettingsOption(
                    title = when (language) {
                        AppLanguage.ENGLISH -> stringResource(R.string.language_english)
                        AppLanguage.SWAHILI -> stringResource(R.string.language_swahili)
                    },
                    subtitle = language.languageTag.uppercase(),
                    icon = Icons.Filled.Language,
                    selected = uiState.selectedLanguage == language,
                    onClick = { onLanguageSelected(language) },
                )
            }
            item {
                CryptoPaySectionHeader(
                    title = stringResource(R.string.appearance),
                    supportingText = stringResource(R.string.settings_theme_hint),
                    modifier = Modifier.padding(top = 16.dp, bottom = 2.dp),
                )
            }
            items(AppTheme.entries, key = AppTheme::storedValue) { theme ->
                SettingsOption(
                    title = when (theme) {
                        AppTheme.LIGHT -> stringResource(R.string.theme_light)
                        AppTheme.DARK -> stringResource(R.string.theme_dark)
                        AppTheme.SYSTEM -> stringResource(R.string.theme_system)
                    },
                    subtitle = when (theme) {
                        AppTheme.LIGHT -> stringResource(R.string.theme_light_hint)
                        AppTheme.DARK -> stringResource(R.string.theme_dark_hint)
                        AppTheme.SYSTEM -> stringResource(R.string.theme_system_hint)
                    },
                    icon = when (theme) {
                        AppTheme.LIGHT -> Icons.Filled.LightMode
                        AppTheme.DARK -> Icons.Filled.DarkMode
                        AppTheme.SYSTEM -> Icons.Filled.SettingsBrightness
                    },
                    selected = uiState.selectedTheme == theme,
                    onClick = { onThemeSelected(theme) },
                )
            }
        }
    }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            shape = RoundedCornerShape(8.dp),
            title = { Text(stringResource(R.string.logout_question)) },
            text = { Text(stringResource(R.string.logout_confirmation)) },
            confirmButton = {
                TextButton(onClick = { showLogoutConfirmation = false; onLogout() }) {
                    Text(stringResource(R.string.logout), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun SettingsOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp))
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (selected) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
