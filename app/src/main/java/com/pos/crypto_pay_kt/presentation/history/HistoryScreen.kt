package com.pos.crypto_pay_kt.presentation.history

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.pos.crypto_pay_kt.presentation.components.DashboardInkPanel
import com.pos.crypto_pay_kt.presentation.components.DashboardSectionHeader
import com.pos.crypto_pay_kt.presentation.components.DashboardStatusBadge

private enum class HistoryFilter { All, Completed, Pending }

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
            HistoryUiState.Loading -> LoadingHistory(Modifier.fillMaxSize().padding(padding))
            is HistoryUiState.Error -> CryptoPayEmptyState(
                icon = Icons.Filled.WifiOff,
                title = stringResource(R.string.history_unavailable),
                supportingText = uiState.message,
                modifier = Modifier.fillMaxSize().padding(padding),
                actionLabel = stringResource(R.string.try_again),
                onAction = onRetry,
            )
            is HistoryUiState.Content -> HistoryContent(
                transactions = uiState.transactions,
                onTransactionSelected = onTransactionSelected,
                modifier = Modifier.fillMaxSize().padding(padding),
            )
        }
    }
}

@Composable
private fun LoadingHistory(modifier: Modifier = Modifier) {
    Column(
        modifier,
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
}

@Composable
private fun HistoryContent(
    transactions: List<Transaction>,
    onTransactionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedFilter by rememberSaveable { mutableStateOf(HistoryFilter.All) }
    val completedCount = transactions.count { it.status.equals("completed", ignoreCase = true) }
    val filteredTransactions = when (selectedFilter) {
        HistoryFilter.All -> transactions
        HistoryFilter.Completed -> transactions.filter { it.status.equals("completed", ignoreCase = true) }
        HistoryFilter.Pending -> transactions.filter { it.status.equals("pending", ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            HistorySummary(
                transactionCount = transactions.size,
                completedCount = completedCount,
            )
        }
        item {
            DashboardSectionHeader(
                title = stringResource(R.string.payment_activity),
                supportingText = stringResource(R.string.payment_activity_hint),
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HistoryFilter.entries.forEach { filter ->
                    HistoryFilterButton(
                        filter = filter,
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        if (filteredTransactions.isEmpty()) {
            item {
                CryptoPayEmptyState(
                    icon = Icons.Filled.ReceiptLong,
                    title = if (transactions.isEmpty()) {
                        stringResource(R.string.no_transactions)
                    } else {
                        stringResource(R.string.no_matching_transactions)
                    },
                    supportingText = if (transactions.isEmpty()) {
                        stringResource(R.string.no_transactions_hint)
                    } else {
                        stringResource(R.string.no_matching_transactions_hint)
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
                )
            }
        } else {
            items(filteredTransactions, key = Transaction::id) { transaction ->
                TransactionRow(transaction, onClick = { onTransactionSelected(transaction.id) })
            }
        }
    }
}

@Composable
private fun HistorySummary(transactionCount: Int, completedCount: Int) {
    DashboardInkPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.transaction_summary),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.68f),
            )
            DashboardStatusBadge(stringResource(R.string.live_status))
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            SummaryMetric(
                value = transactionCount.toString(),
                label = stringResource(R.string.total_transactions),
                modifier = Modifier.weight(1f),
            )
            SummaryMetric(
                value = completedCount.toString(),
                label = stringResource(R.string.settled_transactions),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SummaryMetric(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            label,
            modifier = Modifier.padding(top = 3.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.58f),
        )
    }
}

@Composable
private fun HistoryFilterButton(
    filter: HistoryFilter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = when (filter) {
        HistoryFilter.All -> stringResource(R.string.all)
        HistoryFilter.Completed -> stringResource(R.string.completed)
        HistoryFilter.Pending -> stringResource(R.string.pending)
    }
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TransactionRow(transaction: Transaction, onClick: () -> Unit) {
    val statusColor = when (transaction.status.lowercase()) {
        "completed" -> Color(0xFF087A55)
        "pending" -> Color(0xFF9A5A00)
        else -> MaterialTheme.colorScheme.error
    }
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.12f),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.ArrowDownward, null, tint = statusColor)
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(transaction.type, fontWeight = FontWeight.SemiBold)
                Text(
                    transaction.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(transaction.amount, fontWeight = FontWeight.Bold)
                Text(
                    transaction.status,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                null,
                modifier = Modifier.padding(start = 8.dp).size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
