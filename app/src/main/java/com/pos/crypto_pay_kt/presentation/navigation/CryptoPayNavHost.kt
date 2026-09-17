package com.pos.crypto_pay_kt.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pos.crypto_pay_kt.domain.model.SessionStatus
import com.pos.crypto_pay_kt.presentation.auth.LoginRoute
import com.pos.crypto_pay_kt.presentation.charge.ChargeRoute
import com.pos.crypto_pay_kt.presentation.history.HistoryRoute
import com.pos.crypto_pay_kt.presentation.history.TransactionDetailsRoute
import com.pos.crypto_pay_kt.presentation.home.HomeScreen
import com.pos.crypto_pay_kt.presentation.pos.TapCardPlaceholderScreen
import com.pos.crypto_pay_kt.presentation.refund.RefundRoute
import com.pos.crypto_pay_kt.presentation.settings.SettingsRoute
import com.pos.crypto_pay_kt.presentation.settings.SettingsViewModel
import kotlinx.serialization.Serializable

@Serializable private data object LoginDestination
@Serializable private data object HomeDestination
@Serializable private data object SettingsDestination
@Serializable private data object ChargeDestination
@Serializable private data object HistoryDestination
@Serializable private data object TapCardDestination
@Serializable private data class TransactionDetailsDestination(val transactionId: String)
@Serializable private data class RefundDestination(val transactionId: String)

@Composable
fun CryptoPayNavHost(sessionStatus: SessionStatus) {
    if (sessionStatus is SessionStatus.Checking) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    val navController = rememberNavController()
    val isAuthenticated = sessionStatus is SessionStatus.Authenticated
    val initiallyAuthenticated = remember { isAuthenticated }
    LaunchedEffect(isAuthenticated, initiallyAuthenticated) {
        if (isAuthenticated == initiallyAuthenticated) return@LaunchedEffect
        if (isAuthenticated) {
            navController.navigate(HomeDestination) {
                popUpTo<LoginDestination> { inclusive = true }
                launchSingleTop = true
            }
        } else {
            navController.navigate(LoginDestination) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) HomeDestination else LoginDestination,
    ) {
        composable<LoginDestination> { LoginRoute() }
        composable<HomeDestination> {
            val session = (sessionStatus as? SessionStatus.Authenticated)?.session ?: return@composable
            HomeScreen(
                session = session,
                onCharge = { navController.navigate(ChargeDestination) },
                onTapCard = { navController.navigate(TapCardDestination) },
                onHistory = { navController.navigate(HistoryDestination) },
                onSettings = { navController.navigate(SettingsDestination) },
            )
        }
        composable<SettingsDestination> {
            SettingsRoute(
                viewModel = hiltViewModel<SettingsViewModel>(),
                onBack = navController::popBackStack,
            )
        }
        composable<ChargeDestination> { ChargeRoute(onBack = navController::popBackStack) }
        composable<TapCardDestination> { TapCardPlaceholderScreen(onBack = navController::popBackStack) }
        composable<HistoryDestination> {
            HistoryRoute(
                onBack = navController::popBackStack,
                onTransactionSelected = { navController.navigate(TransactionDetailsDestination(it)) },
            )
        }
        composable<TransactionDetailsDestination> { backStackEntry ->
            val destination = backStackEntry.toRoute<TransactionDetailsDestination>()
            TransactionDetailsRoute(
                transactionId = destination.transactionId,
                onBack = navController::popBackStack,
                onReverse = { navController.navigate(RefundDestination(it)) },
            )
        }
        composable<RefundDestination> { backStackEntry ->
            val destination = backStackEntry.toRoute<RefundDestination>()
            RefundRoute(
                transactionId = destination.transactionId,
                onBack = navController::popBackStack,
                onSuccess = {
                    navController.navigate(HistoryDestination) {
                        popUpTo<HistoryDestination> { inclusive = true }
                    }
                },
            )
        }
    }
}
