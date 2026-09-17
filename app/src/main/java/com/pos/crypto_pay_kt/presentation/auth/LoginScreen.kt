package com.pos.crypto_pay_kt.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pos.crypto_pay_kt.BuildConfig
import com.pos.crypto_pay_kt.R
import com.pos.crypto_pay_kt.presentation.components.CryptoPayButton
import com.pos.crypto_pay_kt.presentation.components.CryptoPayMessage

@Composable
fun LoginRoute(viewModel: LoginViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LoginScreen(
        uiState = uiState,
        onPinChanged = viewModel::updatePin,
        onSubmit = viewModel::submit,
    )
}

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onPinChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { LoginBrandHeader() }
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.enter_pin),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.pin_prompt),
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                PinIndicator(
                    enteredDigits = uiState.pin.length,
                    isError = uiState.errorMessage != null,
                    modifier = Modifier.padding(top = 22.dp),
                )
                if (uiState.errorMessage != null) {
                    CryptoPayMessage(
                        text = uiState.errorMessage,
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }
                NumberPad(
                    enabled = !uiState.isLoading,
                    onNumber = { digit ->
                        if (uiState.pin.length < 4) onPinChanged(uiState.pin + digit)
                    },
                    onBackspace = { onPinChanged(uiState.pin.dropLast(1)) },
                    modifier = Modifier.padding(top = 18.dp),
                )
                CryptoPayButton(
                    label = stringResource(R.string.sign_in),
                    onClick = onSubmit,
                    modifier = Modifier.padding(top = 20.dp),
                    icon = Icons.Filled.Lock,
                    enabled = uiState.pin.length == 4,
                    loading = uiState.isLoading,
                )
                Text(
                    text = stringResource(R.string.terminal_label, BuildConfig.TERMINAL_ID),
                    modifier = Modifier.padding(top = 14.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LoginBrandHeader() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.crypto_pay_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(82.dp),
            )
            Text(
                text = stringResource(R.string.app_name),
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.merchant_terminal),
                modifier = Modifier.padding(top = 2.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f),
            )
        }
    }
}

@Composable
private fun PinIndicator(
    enteredDigits: Int,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(4) { index ->
            val entered = index < enteredDigits
            val color = when {
                isError -> MaterialTheme.colorScheme.error
                entered -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outlineVariant
            }
            Surface(
                modifier = Modifier.size(width = 48.dp, height = 52.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (entered) color else MaterialTheme.colorScheme.surfaceContainerLow,
                border = if (entered) null else androidx.compose.foundation.BorderStroke(1.dp, color),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (entered) {
                        Surface(modifier = Modifier.size(10.dp), shape = CircleShape, color = MaterialTheme.colorScheme.onPrimary) {}
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberPad(
    enabled: Boolean,
    onNumber: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
        ).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { digit -> NumberKey(digit, enabled) { onNumber(digit) } }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.size(64.dp))
            NumberKey("0", enabled) { onNumber("0") }
            Surface(
                modifier = Modifier
                    .size(64.dp)
                    .clickable(enabled = enabled, role = Role.Button, onClick = onBackspace),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerLow,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = stringResource(R.string.delete_digit),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberKey(number: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(64.dp)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(number, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}
