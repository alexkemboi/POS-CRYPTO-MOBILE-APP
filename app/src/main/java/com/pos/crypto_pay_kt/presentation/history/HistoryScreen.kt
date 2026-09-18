package com.pos.crypto_pay_kt.presentation.history

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pos.crypto_pay_kt.R
import com.pos.crypto_pay_kt.domain.model.Transaction
import com.pos.crypto_pay_kt.presentation.components.CryptoPayEmptyState
import com.pos.crypto_pay_kt.presentation.components.CryptoPayTopBar

@Composable
fun HistoryRoute(
    onBack: () -> Unit,
    onTransactionSelected: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HistoryScreen(uiState, onBack, onTransactionSelected, viewModel::load)
}

@Composable
private fun HistoryScreen(
    uiState: HistoryUiState,
    onBack: () -> Unit,
    onTransactionSelected: (String) -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(topBar = { CryptoPayTopBar(stringResource(R.string.transaction_history), onBack = onBack) }) { padding ->
        when (uiState) {
            HistoryUiState.Loading -> Column(
                Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(strokeWidth = 3.dp)
                Text(
                    stringResource(R.string.loading_transactions),
                    modifier = Modifier.padding(top = 14.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            is HistoryUiState.Error -> CryptoPayEmptyState(
                icon = Icons.Filled.WifiOff,
                title = stringResource(R.string.history_unavailable),
                supportingText = uiState.message,
                modifier = Modifier.fillMaxSize().padding(padding),
                actionLabel = stringResource(R.string.try_again),
                onAction = onRetry,
            )
            is HistoryUiState.Content -> {
                if (uiState.transactions.isEmpty()) {
                    CryptoPayEmptyState(
                        icon = Icons.Filled.ReceiptLong,
                        title = stringResource(R.string.no_transactions),
                        supportingText = stringResource(R.string.no_transactions_hint),
                        modifier = Modifier.fillMaxSize().padding(padding),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(uiState.transactions, key = Transaction::id) { transaction ->
                            TransactionRow(transaction, onClick = { onTransactionSelected(transaction.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction, onClick: () -> Unit) {
    val statusColor = when (transaction.status.lowercase()) {
        "completed" -> Color(0xFF16845B)
        "pending" -> Color(0xFFB26A00)
        else -> MaterialTheme.colorScheme.error
    }
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(modifier = Modifier.size(44.dp), shape = RoundedCornerShape(8.dp), color = statusColor.copy(alpha = 0.12f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.ArrowDownward, null, tint = statusColor)
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(transaction.type, fontWeight = FontWeight.SemiBold)
                Text(transaction.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(transaction.amount, fontWeight = FontWeight.Bold, color = statusColor)
                Surface(
                    modifier = Modifier.padding(top = 5.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f),
                ) {
                    Text(
                        transaction.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
