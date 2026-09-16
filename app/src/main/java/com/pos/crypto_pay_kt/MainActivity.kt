package com.pos.crypto_pay_kt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pos.crypto_pay_kt.presentation.MainViewModel
import com.pos.crypto_pay_kt.presentation.navigation.CryptoPayNavHost
import com.pos.crypto_pay_kt.ui.theme.CryptoPayTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = hiltViewModel<MainViewModel>()
            val theme by viewModel.theme.collectAsStateWithLifecycle()

            CryptoPayTheme(appTheme = theme) {
                CryptoPayNavHost()
            }
        }
    }
}
