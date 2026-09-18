package com.pos.crypto_pay_kt.presentation.charge

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pos.crypto_pay_kt.R
import com.pos.crypto_pay_kt.domain.model.PaymentCurrency
import com.pos.crypto_pay_kt.domain.model.PaymentMethod
import com.pos.crypto_pay_kt.presentation.components.CryptoPayButton
import com.pos.crypto_pay_kt.presentation.components.CryptoPayField
import com.pos.crypto_pay_kt.presentation.components.CryptoPayMessage
import com.pos.crypto_pay_kt.presentation.components.CryptoPaySectionHeader
import com.pos.crypto_pay_kt.presentation.components.CryptoPayTopBar

@Composable
fun ChargeRoute(
    onBack: () -> Unit,
    viewModel: ChargeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(viewModel, uriHandler) {
        viewModel.events.collect { event ->
            when (event) {
                is ChargeEvent.OpenCheckout -> runCatching { uriHandler.openUri(event.url) }
                    .onFailure { viewModel.checkoutOpenFailed() }
            }
        }
    }
    ChargeScreen(
        uiState = uiState,
        onBack = onBack,
        onAmountChanged = viewModel::updateAmount,
        onPhoneChanged = viewModel::updatePhone,
        onMethodSelected = viewModel::selectMethod,
        onCurrencySelected = viewModel::selectCurrency,
        onDismissIntro = viewModel::dismissIntro,
        onSubmit = viewModel::submit,
        onReopenCheckout = viewModel::reopenCheckout,
        onReset = viewModel::reset,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChargeScreen(
    uiState: ChargeUiState,
    onBack: () -> Unit,
    onAmountChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onMethodSelected: (PaymentMethod) -> Unit,
    onCurrencySelected: (PaymentCurrency) -> Unit,
    onDismissIntro: () -> Unit,
    onSubmit: () -> Unit,
    onReopenCheckout: () -> Unit,
    onReset: () -> Unit,
) {
    Scaffold(topBar = { CryptoPayTopBar(stringResource(R.string.charge_payment), onBack = onBack) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (uiState.showIntro) {
                    item { ChargeIntro(onDismissIntro) }
                }
                item {
                    CryptoPaySectionHeader(
                        title = stringResource(R.string.payment_method),
                        supportingText = stringResource(R.string.payment_method_hint),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        PaymentMethodOption(
                            icon = Icons.Filled.CreditCard,
                            title = stringResource(R.string.card),
                            subtitle = stringResource(R.string.secure_checkout),
                            selected = uiState.paymentMethod == PaymentMethod.CARD,
                            onClick = { onMethodSelected(PaymentMethod.CARD) },
                            modifier = Modifier.weight(1f),
                        )
                        PaymentMethodOption(
                            icon = Icons.Filled.PhoneAndroid,
                            title = stringResource(R.string.mpesa),
                            subtitle = stringResource(R.string.stk_push),
                            selected = uiState.paymentMethod == PaymentMethod.MPESA,
                            onClick = { onMethodSelected(PaymentMethod.MPESA) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                item {
                    CryptoPaySectionHeader(
                        title = stringResource(R.string.payment_details),
                        supportingText = stringResource(R.string.payment_details_hint),
                    )
                }
                item {
                    CryptoPayField(
                        value = uiState.amount,
                        onValueChange = onAmountChanged,
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.amount),
                        leadingIcon = Icons.Filled.Payments,
                        prefix = uiState.currency.apiValue,
                        placeholder = "0.00",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                }
                item {
                    CurrencySelector(uiState.currency, onCurrencySelected)
                }
                if (uiState.paymentMethod == PaymentMethod.MPESA) {
                    item {
                        CryptoPayField(
                            value = uiState.phone,
                            onValueChange = onPhoneChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = stringResource(R.string.customer_phone),
                            placeholder = stringResource(R.string.phone_hint),
                            leadingIcon = Icons.Filled.PhoneAndroid,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        )
                    }
                }
                uiState.errorMessage?.let { message ->
                    item {
                        CryptoPayMessage(message)
                    }
                }
                item {
                    CryptoPayButton(
                        label = stringResource(R.string.charge),
                        onClick = onSubmit,
                        icon = if (uiState.paymentMethod == PaymentMethod.CARD) Icons.Filled.CreditCard else Icons.Filled.PhoneAndroid,
                    )
                }
            }
            when (uiState.phase) {
                ChargePhase.SUBMITTING -> PaymentOverlay(
                    icon = { CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary) },
                    message = uiState.statusMessage,
                )
                ChargePhase.AWAITING -> PaymentOverlay(
                    icon = { Icon(Icons.Filled.ReceiptLong, null, Modifier.size(52.dp)) },
                    message = uiState.statusMessage,
                    reference = uiState.transactionId,
                    action = uiState.checkoutUrl?.let {
                        { TextButton(onClick = onReopenCheckout) { Icon(Icons.Filled.OpenInBrowser, null); Text(stringResource(R.string.open_checkout), Modifier.padding(start = 8.dp)) } }
                    },
                    secondaryAction = { TextButton(onClick = onReset) { Text(stringResource(R.string.dismiss)) } },
                )
                ChargePhase.SUCCESS -> PaymentOverlay(
                    icon = { Icon(Icons.Filled.CheckCircle, null, Modifier.size(64.dp), tint = Color(0xFF5EEAD4)) },
                    message = stringResource(R.string.payment_successful),
                    reference = uiState.transactionId,
                    action = { Button(onClick = onReset) { Text(stringResource(R.string.confirm)) } },
                )
                ChargePhase.FORM -> Unit
            }
        }
    }
}

@Composable
private fun ChargeIntro(onDismiss: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Filled.CreditCard, null, Modifier.size(34.dp))
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(stringResource(R.string.charge_customer), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.charge_intro), modifier = Modifier.padding(top = 6.dp), color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.76f))
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, stringResource(R.string.hide))
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(104.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.Center) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                if (selected) {
                    Icon(Icons.Filled.CheckCircle, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
            Text(title, modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CurrencySelector(
    selectedCurrency: PaymentCurrency,
    onCurrencySelected: (PaymentCurrency) -> Unit,
) {
    Column {
        Text(
            stringResource(R.string.currency),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            PaymentCurrency.entries.forEach { currency ->
                val selected = selectedCurrency == currency
                Surface(
                    modifier = Modifier.weight(1f).height(48.dp).clickable { onCurrencySelected(currency) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                    border = androidx.compose.foundation.BorderStroke(
                        if (selected) 2.dp else 1.dp,
                        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    ),
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (selected) {
                            Icon(Icons.Filled.Check, null, Modifier.size(17.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.size(6.dp))
                        }
                        Text(currency.apiValue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentOverlay(
    icon: @Composable () -> Unit,
    message: String,
    reference: String = "",
    action: (@Composable () -> Unit)? = null,
    secondaryAction: (@Composable () -> Unit)? = null,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.97f),
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            icon()
            Text(message, modifier = Modifier.padding(top = 18.dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (reference.isNotBlank()) {
                Text(stringResource(R.string.transaction_reference, reference), modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f))
            }
            Spacer(Modifier.height(20.dp))
            action?.invoke()
            secondaryAction?.invoke()
        }
    }
}
