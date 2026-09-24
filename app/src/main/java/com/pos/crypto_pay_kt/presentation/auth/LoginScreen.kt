package com.pos.crypto_pay_kt.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

private val LoginInk = Color(0xFF0C0D0F)
private val LoginAccent = Color(0xFF43D7E6)
private val LoginSuccess = Color(0xFF48D6A2)
private val LoginError = Color(0xFFFFB4AB)

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
    Box(modifier = Modifier.fillMaxSize().background(LoginInk)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 22.dp),
        ) {
            item { TerminalHeader() }
            item {
                Text(
                    text = stringResource(R.string.welcome_back),
                    modifier = Modifier.padding(top = 38.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.sign_in_subtitle),
                    modifier = Modifier.padding(top = 6.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.62f),
                )
            }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(top = 28.dp),
                    color = Color.White.copy(alpha = 0.12f),
                )
            }
            item {
                PinSection(
                    enteredDigits = uiState.pin.length,
                    isError = uiState.errorMessage != null,
                    modifier = Modifier.padding(top = 22.dp),
                )
            }
            item {
                AnimatedVisibility(
                    visible = uiState.errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    LoginErrorMessage(
                        message = uiState.errorMessage.orEmpty(),
                        modifier = Modifier.padding(top = 14.dp),
                    )
                }
            }
            item {
                SecureNumberPad(
                    enabled = !uiState.isLoading,
                    canDelete = uiState.pin.isNotEmpty(),
                    onNumber = { digit ->
                        if (uiState.pin.length < 4) onPinChanged(uiState.pin + digit)
                    },
                    onBackspace = { onPinChanged(uiState.pin.dropLast(1)) },
                    modifier = Modifier.padding(top = 18.dp),
                )
            }
            item {
                LoginSubmitButton(
                    enabled = uiState.pin.length == 4,
                    loading = uiState.isLoading,
                    onClick = onSubmit,
                    modifier = Modifier.padding(top = 20.dp),
                )
            }
            item {
                Text(
                    text = stringResource(R.string.terminal_label, BuildConfig.TERMINAL_ID),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.38f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun TerminalHeader() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.crypto_pay_logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier.size(54.dp),
        )
        Column(Modifier.weight(1f).padding(start = 12.dp)) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.merchant_terminal),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.52f),
            )
        }
    }
}

@Composable
private fun PinSection(
    enteredDigits: Int,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.pin_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.pin_prompt),
                    modifier = Modifier.padding(top = 2.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                )
            }
            Text(
                text = stringResource(R.string.pin_progress, enteredDigits),
                style = MaterialTheme.typography.labelMedium,
                color = if (isError) LoginError else Color.White.copy(alpha = 0.48f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(4) { index ->
                val segmentColor by animateColorAsState(
                    targetValue = when {
                        isError -> LoginError
                        index < enteredDigits -> LoginAccent
                        else -> Color.White.copy(alpha = 0.13f)
                    },
                    label = "pinSegment$index",
                )
                Surface(
                    modifier = Modifier.weight(1f).height(6.dp),
                    shape = RoundedCornerShape(3.dp),
                    color = segmentColor,
                ) {}
            }
        }
    }
}

@Composable
private fun LoginErrorMessage(message: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = LoginError.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, LoginError.copy(alpha = 0.28f)),
        contentColor = LoginError,
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun SecureNumberPad(
    enabled: Boolean,
    canDelete: Boolean,
    onNumber: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
        ).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { digit ->
                    NumberKey(
                        number = digit,
                        enabled = enabled,
                        onClick = { onNumber(digit) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.weight(1f).height(58.dp))
            NumberKey(
                number = "0",
                enabled = enabled,
                onClick = { onNumber("0") },
                modifier = Modifier.weight(1f),
            )
            KeySurface(
                enabled = enabled && canDelete,
                onClick = onBackspace,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = stringResource(R.string.delete_digit),
                    modifier = Modifier.size(22.dp),
                    tint = Color.White.copy(alpha = if (canDelete) 0.9f else 0.25f),
                )
            }
        }
    }
}

@Composable
private fun NumberKey(
    number: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KeySurface(enabled = enabled, onClick = onClick, modifier = modifier) {
        Text(
            text = number,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White.copy(alpha = if (enabled) 0.94f else 0.34f),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun KeySurface(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier
            .height(58.dp)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.07f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.11f)),
    ) {
        Box(contentAlignment = Alignment.Center) { content() }
    }
}

@Composable
private fun LoginSubmitButton(
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(54.dp),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = LoginInk,
            disabledContainerColor = Color.White.copy(alpha = 0.14f),
            disabledContentColor = Color.White.copy(alpha = 0.36f),
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(21.dp), color = LoginInk, strokeWidth = 2.dp)
        } else {
            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text(stringResource(R.string.sign_in), fontWeight = FontWeight.Bold)
        }
    }
}
