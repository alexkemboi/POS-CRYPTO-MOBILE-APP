package com.pos.crypto_pay_kt.presentation.pos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pos.crypto_pay_kt.R
import com.pos.crypto_pay_kt.presentation.components.CryptoPayEmptyState
import com.pos.crypto_pay_kt.presentation.components.CryptoPayTopBar

@Composable
fun TapCardPlaceholderScreen(onBack: () -> Unit) {
    Scaffold(topBar = { CryptoPayTopBar(stringResource(R.string.tap_card), onBack = onBack) }) { padding ->
        CryptoPayEmptyState(
            icon = Icons.Filled.Contactless,
            title = stringResource(R.string.coming_soon),
            supportingText = stringResource(R.string.tap_card_deferred),
            modifier = Modifier.fillMaxSize().padding(padding),
        )
    }
}
