package com.pos.crypto_pay_kt.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pos.crypto_pay_kt.presentation.auth.SignedOutScreen
import com.pos.crypto_pay_kt.presentation.settings.SettingsRoute
import com.pos.crypto_pay_kt.presentation.settings.SettingsViewModel
import kotlinx.serialization.Serializable

@Serializable
private data object SettingsDestination

@Serializable
private data object SignedOutDestination

@Composable
fun CryptoPayNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SettingsDestination,
    ) {
        composable<SettingsDestination> {
            val viewModel = hiltViewModel<SettingsViewModel>()
            SettingsRoute(
                viewModel = viewModel,
                onLoggedOut = {
                    navController.navigate(SignedOutDestination) {
                        popUpTo<SettingsDestination> {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable<SignedOutDestination> {
            SignedOutScreen()
        }
    }
}
