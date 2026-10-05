package com.pos.crypto_pay_kt.presentation.charge

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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

private val ChargeInk = Color(0xFF101114)
private val SuccessGreen = Color(0xFF48D6A2)

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
    Scaffold(
        topBar = { CryptoPayTopBar(stringResource(R.string.accept_payment), onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                if (uiState.showIntro) {
                    item { ChargeIntro(onDismissIntro) }
                }
                item {
                    PaymentMethodSelector(
                        selectedMethod = uiState.paymentMethod,
                        onMethodSelected = onMethodSelected,
                    )
                }
                item {
                    Column {
                        CryptoPaySectionHeader(
                            title = stringResource(R.string.payment_details),
                            supportingText = stringResource(R.string.payment_details_hint),
                        )
                        PaymentAmountField(
                            value = uiState.amount,
                            onValueChange = onAmountChanged,
                            currency = uiState.currency,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                        CurrencySelector(
                            selectedCurrency = uiState.currency,
                            onCurrencySelected = onCurrencySelected,
                            modifier = Modifier.padding(top = 14.dp),
                        )
                    }
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
                    item { CryptoPayMessage(message) }
                }
                item {
                    CryptoPayButton(
                        label = if (uiState.paymentMethod == PaymentMethod.CARD) {
                            stringResource(R.string.continue_to_checkout)
                        } else {
                            stringResource(R.string.send_mpesa_prompt)
                        },
                        onClick = onSubmit,
                        icon = if (uiState.paymentMethod == PaymentMethod.CARD) {
                            Icons.Filled.OpenInBrowser
                        } else {
                            Icons.Filled.PhoneAndroid
                        },
                    )
                }
            }
            when (uiState.phase) {
                ChargePhase.SUBMITTING -> PaymentOverlay(
                    icon = { CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp) },
                    message = uiState.statusMessage,
                )
                ChargePhase.AWAITING -> PaymentOverlay(
                    icon = { Icon(Icons.Filled.ReceiptLong, null, Modifier.size(52.dp)) },
                    message = uiState.statusMessage,
                    reference = uiState.transactionId,
                    actionLabel = uiState.checkoutUrl?.let { stringResource(R.string.open_checkout) },
                    onAction = uiState.checkoutUrl?.let { onReopenCheckout },
                    secondaryLabel = stringResource(R.string.dismiss),
                    onSecondaryAction = onReset,
                )
                ChargePhase.SUCCESS -> PaymentOverlay(
                    icon = { Icon(Icons.Filled.CheckCircle, null, Modifier.size(64.dp), tint = SuccessGreen) },
                    message = stringResource(R.string.payment_successful),
                    reference = uiState.transactionId,
                    actionLabel = stringResource(R.string.confirm),
                    onAction = onReset,
                )
                ChargePhase.FORM -> Unit
            }
        }
    }
}

@Composable
private fun ChargeIntro(onDismiss: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Lock, null, Modifier.size(19.dp))
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(
                    stringResource(R.string.charge_customer),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    stringResource(R.string.charge_intro),
                    modifier = Modifier.padding(top = 3.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(38.dp)) {
                Icon(Icons.Filled.Close, stringResource(R.string.hide), Modifier.size(20.dp))
            }
        }
        HorizontalDivider(Modifier.padding(top = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun PaymentMethodSelector(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit,
) {
    Column {
        CryptoPaySectionHeader(
            title = stringResource(R.string.payment_method),
            supportingText = stringResource(R.string.payment_method_hint),
        )
        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Row(Modifier.padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                PaymentMethodSegment(
                    icon = Icons.Filled.CreditCard,
                    title = stringResource(R.string.card),
                    subtitle = stringResource(R.string.secure_checkout),
                    selected = selectedMethod == PaymentMethod.CARD,
                    onClick = { onMethodSelected(PaymentMethod.CARD) },
                    modifier = Modifier.weight(1f),
                )
                PaymentMethodSegment(
                    icon = Icons.Filled.PhoneAndroid,
                    title = stringResource(R.string.mpesa),
                    subtitle = stringResource(R.string.stk_push),
                    selected = selectedMethod == PaymentMethod.MPESA,
                    onClick = { onMethodSelected(PaymentMethod.MPESA) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodSegment(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Surface(
        modifier = modifier
            .height(72.dp)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, null, Modifier.size(22.dp))
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.66f),
                )
            }
            if (selected) Icon(Icons.Filled.Check, null, Modifier.size(18.dp))
        }
    }
}

@Composable
private fun PaymentAmountField(
    value: String,
    onValueChange: (String) -> Unit,
    currency: PaymentCurrency,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (focused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        label = "amountBorder",
    )
    Column(modifier) {
        Text(
            stringResource(R.string.amount),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Surface(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(if (focused) 2.dp else 1.dp, borderColor),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 17.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    currency.apiValue,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(Modifier.weight(1f).padding(start = 12.dp)) {
                    if (value.isEmpty()) {
                        Text(
                            "0.00",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.48f),
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth().onFocusChanged { focused = it.isFocused },
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencySelector(
    selectedCurrency: PaymentCurrency,
    onCurrencySelected: (PaymentCurrency) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text(
            stringResource(R.string.currency),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PaymentCurrency.entries.forEach { currency ->
                val selected = selectedCurrency == currency
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .selectable(
                            selected = selected,
                            role = Role.RadioButton,
                            onClick = { onCurrencySelected(currency) },
                        ),
                    shape = RoundedCornerShape(8.dp),
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    border = if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Box(contentAlignment = Alignment.Center) {
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
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    secondaryLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ChargeInk,
        contentColor = Color.White,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            icon()
            Text(
                message,
                modifier = Modifier.padding(top = 20.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            if (reference.isNotBlank()) {
                Text(
                    stringResource(R.string.transaction_reference, reference),
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White.copy(alpha = 0.62f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (actionLabel != null && onAction != null) {
                Button(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = ChargeInk),
                ) {
                    if (actionLabel == stringResource(R.string.open_checkout)) {
                        Icon(Icons.Filled.OpenInBrowser, null, Modifier.size(19.dp))
                        Spacer(Modifier.size(8.dp))
                    }
                    Text(actionLabel, fontWeight = FontWeight.Bold)
                }
            }
            if (secondaryLabel != null && onSecondaryAction != null) {
                TextButton(
                    onClick = onSecondaryAction,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                ) {
                    Text(secondaryLabel, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
