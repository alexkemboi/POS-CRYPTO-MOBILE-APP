package com.pos.crypto_pay_kt.presentation.history

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pos.crypto_pay_kt.R
import com.pos.crypto_pay_kt.domain.model.Transaction
import com.pos.crypto_pay_kt.presentation.components.CryptoPayButton
import com.pos.crypto_pay_kt.presentation.components.CryptoPayTopBar

@Composable
fun TransactionDetailsRoute(
    transactionId: String,
    onBack: () -> Unit,
    onReverse: (String) -> Unit,
    viewModel: TransactionDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(transactionId) { viewModel.load(transactionId) }
    val transaction by viewModel.transaction.collectAsStateWithLifecycle()
    Scaffold(topBar = { CryptoPayTopBar(stringResource(R.string.transaction_details), onBack = onBack) }) { padding ->
        val value = transaction
        if (value == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            TransactionDetails(value, onReverse, Modifier.padding(padding))
        }
    }
}

@Composable
private fun TransactionDetails(transaction: Transaction, onReverse: (String) -> Unit, modifier: Modifier = Modifier) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.ArrowDownward, null, Modifier.size(38.dp), tint = MaterialTheme.colorScheme.primary) }
        }
        Text(transaction.amount, modifier = Modifier.padding(top = 14.dp), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(transaction.fiat, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 1.dp,
        ) {
            Column {
                DetailRow(stringResource(R.string.status), transaction.status)
                HorizontalDivider()
                DetailRow(stringResource(R.string.date), transaction.date)
                HorizontalDivider()
                DetailRow(stringResource(R.string.transaction_id), transaction.id)
                HorizontalDivider()
                DetailRow(stringResource(R.string.type), transaction.type)
                HorizontalDivider()
                DetailRow(stringResource(R.string.network_fee), "0.0001 ${transaction.crypto}")
                HorizontalDivider()
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.address), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(transaction.address, modifier = Modifier.padding(top = 4.dp), style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(transaction.address))
                            Toast.makeText(context, context.getString(R.string.address_copied), Toast.LENGTH_SHORT).show()
                        },
                    ) {
                        Icon(Icons.Filled.ContentCopy, stringResource(R.string.copy_address))
                    }
                }
            }
        }
        if (transaction.status.equals("Completed", ignoreCase = true)) {
            CryptoPayButton(
                label = stringResource(R.string.reverse_transaction),
                onClick = { onReverse(transaction.id) },
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                icon = Icons.Filled.Refresh,
                destructive = true,
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, modifier = Modifier.padding(start = 16.dp), fontWeight = FontWeight.Medium)
    }
}
