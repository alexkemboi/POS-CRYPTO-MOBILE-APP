package com.pos.crypto_pay_kt.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pos.crypto_pay_kt.R
import com.pos.crypto_pay_kt.domain.model.UserSession
import com.pos.crypto_pay_kt.presentation.components.CryptoPaySectionHeader
import com.pos.crypto_pay_kt.presentation.components.CryptoPayTopBar

private data class MarketItem(val name: String, val symbol: String, val price: String, val color: Color)
private data class WalletAsset(val name: String, val symbol: String, val balance: String, val fiat: String, val color: Color)

@Composable
fun HomeScreen(
    session: UserSession,
    onCharge: () -> Unit,
    onTapCard: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            CryptoPayTopBar(
                title = if (selectedTab == 0) stringResource(R.string.home) else stringResource(R.string.wallet),
                onSettings = onSettings,
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.Home, null) },
                    label = { Text(stringResource(R.string.home)) },
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Filled.AccountBalanceWallet, null) },
                    label = { Text(stringResource(R.string.wallet)) },
                )
            }
        },
    ) { padding ->
        if (selectedTab == 0) {
            HomeOverview(
                session = session,
                onCharge = onCharge,
                onTapCard = onTapCard,
                onHistory = onHistory,
                modifier = Modifier.padding(padding),
            )
        } else {
            WalletOverview(modifier = Modifier.padding(padding))
        }
    }
}

@Composable
private fun HomeOverview(
    session: UserSession,
    onCharge: () -> Unit,
    onTapCard: () -> Unit,
    onHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val markets = listOf(
        MarketItem("Bitcoin", "BTC", "KES 65,000", Color(0xFFF7931A)),
        MarketItem("Ethereum", "ETH", "KES 3,500", Color(0xFF627EEA)),
        MarketItem("Tether", "USDT", "KES 1.00", Color(0xFF26A17B)),
        MarketItem("Solana", "SOL", "KES 145.00", Color(0xFF7C4DFF)),
        MarketItem("Binance Coin", "BNB", "KES 420.00", Color(0xFFF3BA2F)),
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Storefront, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
                Column(Modifier.padding(start = 12.dp)) {
                    Text(
                        text = stringResource(R.string.hello_agent, session.agentName),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.role_label, session.role),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        item { PortfolioPanel() }
        item {
            CryptoPaySectionHeader(
                title = stringResource(R.string.quick_actions),
                supportingText = stringResource(R.string.quick_actions_hint),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                QuickAction(Icons.Filled.CreditCard, stringResource(R.string.accept_payment), Color(0xFF00796B), onCharge, Modifier.weight(1f))
                QuickAction(Icons.Filled.Contactless, stringResource(R.string.tap_card), Color(0xFF3759A8), onTapCard, Modifier.weight(1f))
                QuickAction(Icons.Filled.History, stringResource(R.string.history), Color(0xFFB26A00), onHistory, Modifier.weight(1f))
            }
        }
        item {
            CryptoPaySectionHeader(
                title = stringResource(R.string.market_snapshot),
                supportingText = stringResource(R.string.market_snapshot_hint),
            )
        }
        items(markets, key = { it.symbol }) { market ->
            MarketRow(market)
            HorizontalDivider()
        }
    }
}

@Composable
private fun PortfolioPanel() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(stringResource(R.string.portfolio_value), color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f))
            Text(
                text = stringResource(R.string.portfolio_amount),
                modifier = Modifier.padding(top = 6.dp),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.portfolio_change), color = Color(0xFF5EEAD4), fontWeight = FontWeight.SemiBold)
                Icon(Icons.Filled.TrendingUp, contentDescription = null)
            }
        }
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(94.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = RoundedCornerShape(8.dp),
                color = accent.copy(alpha = 0.14f),
            ) {
                androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(19.dp), tint = accent)
                }
            }
            Text(
                text = label,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun MarketRow(item: MarketItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(modifier = Modifier.size(42.dp), shape = CircleShape, color = item.color.copy(alpha = 0.14f)) {
            androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.CurrencyBitcoin, null, tint = item.color)
            }
        }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(item.name, fontWeight = FontWeight.SemiBold)
            Text(item.symbol, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(item.price, fontWeight = FontWeight.SemiBold)
            Text("+1.2%", color = Color(0xFF16845B), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun WalletOverview(modifier: Modifier = Modifier) {
    val assets = listOf(
        WalletAsset("Bitcoin", "BTC", "0.15", "KES 9,750.00", Color(0xFFF7931A)),
        WalletAsset("Ethereum", "ETH", "1.2", "KES 4,200.00", Color(0xFF627EEA)),
        WalletAsset("Tether", "USDT", "500.00", "KES 500.00", Color(0xFF26A17B)),
        WalletAsset("Solana", "SOL", "15.5", "KES 2,247.50", Color(0xFF7C4DFF)),
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Surface(shape = RoundedCornerShape(8.dp), tonalElevation = 2.dp) {
                Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.total_balance), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(stringResource(R.string.wallet_total), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Row(Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Icon(Icons.Filled.AccountBalanceWallet, contentDescription = stringResource(R.string.deposit), tint = Color(0xFF16845B))
                        Icon(Icons.Filled.Send, contentDescription = stringResource(R.string.withdraw), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
        item {
            CryptoPaySectionHeader(
                title = stringResource(R.string.your_assets),
                supportingText = stringResource(R.string.your_assets_hint),
            )
        }
        items(assets, key = { it.symbol }) { asset ->
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(42.dp), shape = CircleShape, color = asset.color.copy(alpha = 0.14f)) {
                    androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                        Text(asset.symbol.take(1), fontWeight = FontWeight.Bold, color = asset.color)
                    }
                }
                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(asset.name, fontWeight = FontWeight.SemiBold)
                    Text("${asset.balance} ${asset.symbol}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(asset.fiat, fontWeight = FontWeight.SemiBold)
            }
            HorizontalDivider()
        }
    }
}
