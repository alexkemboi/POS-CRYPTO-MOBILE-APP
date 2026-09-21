package com.pos.crypto_pay_kt.presentation.refund

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pos.crypto_pay_kt.R
import com.pos.crypto_pay_kt.presentation.components.CryptoPayButton
import com.pos.crypto_pay_kt.presentation.components.CryptoPayField
import com.pos.crypto_pay_kt.presentation.components.CryptoPayTopBar

@Composable
fun RefundRoute(
    transactionId: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: RefundViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onSuccess()
    }
    RefundScreen(
        uiState = uiState,
        onBack = onBack,
        onReasonChanged = viewModel::updateReason,
        onSubmit = { viewModel.submit(transactionId) },
    )
}

@Composable
private fun RefundScreen(
    uiState: RefundUiState,
    onBack: () -> Unit,
    onReasonChanged: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Scaffold(topBar = { CryptoPayTopBar(stringResource(R.string.reverse_transaction), onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
            ) {
                Column(Modifier.padding(18.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ) {
                        Icon(Icons.Filled.Refresh, null, Modifier.padding(9.dp))
                    }
                    Text(
                        stringResource(R.string.reverse_question),
                        modifier = Modifier.padding(top = 14.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        stringResource(R.string.reverse_notice),
                        modifier = Modifier.padding(top = 6.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.78f),
                    )
                }
            }
            CryptoPayField(
                value = uiState.reason,
                onValueChange = onReasonChanged,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                label = stringResource(R.string.reversal_reason),
                placeholder = stringResource(R.string.reversal_reason_hint),
                minLines = 4,
                maxLines = 6,
                singleLine = false,
                isError = uiState.errorMessage != null,
                errorMessage = uiState.errorMessage,
            )
            CryptoPayButton(
                label = stringResource(R.string.submit_reversal),
                onClick = onSubmit,
                modifier = Modifier.padding(top = 20.dp),
                icon = Icons.Filled.Refresh,
                loading = uiState.isLoading,
                destructive = true,
            )
        }
    }
}
