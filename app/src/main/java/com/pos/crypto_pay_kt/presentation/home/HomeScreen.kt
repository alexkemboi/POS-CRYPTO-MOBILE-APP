package com.pos.crypto_pay_kt.presentation.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.CurrencyBitcoin
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pos.crypto_pay_kt.BuildConfig
import com.pos.crypto_pay_kt.R
import com.pos.crypto_pay_kt.domain.model.UserSession
import com.pos.crypto_pay_kt.presentation.components.DashboardHeader
import com.pos.crypto_pay_kt.presentation.components.DashboardInkPanel
import com.pos.crypto_pay_kt.presentation.components.DashboardSectionHeader
import com.pos.crypto_pay_kt.presentation.components.DashboardStatusBadge

private data class MarketItem(
    val name: String,
    val symbol: String,
    val price: String,
    val movement: String,
    val positive: Boolean,
    val color: Color,
)

private data class WalletAsset(
    val name: String,
    val symbol: String,
    val balance: String,
    val fiat: String,
    val allocation: Float,
    val color: Color,
)

private data class RecentPayment(
    val title: String,
    val subtitle: String,
    val amount: String,
)

@Composable
fun HomeScreen(
    session: UserSession,
    onCharge: () -> Unit,
    onTapCard: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Filled.Home, null) },
                    label = { Text(stringResource(R.string.home)) },
                    colors = dashboardNavigationColors(),
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Filled.AccountBalanceWallet, null) },
                    label = { Text(stringResource(R.string.wallet)) },
                    colors = dashboardNavigationColors(),
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
                onSettings = onSettings,
                modifier = Modifier.padding(padding),
            )
        } else {
            WalletOverview(
                onSettings = onSettings,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun dashboardNavigationColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
    selectedTextColor = MaterialTheme.colorScheme.onSurface,
    indicatorColor = MaterialTheme.colorScheme.primary,
    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

@Composable
private fun HomeOverview(
    session: UserSession,
    onCharge: () -> Unit,
    onTapCard: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val recentPayments = listOf(
        RecentPayment(stringResource(R.string.card_payment), stringResource(R.string.today_time, "14:30"), "+KES 1,950.00"),
        RecentPayment(stringResource(R.string.crypto_payment), stringResource(R.string.yesterday_time, "09:15"), "+KES 1,200.00"),
    )
    val markets = listOf(
        MarketItem("Bitcoin", "BTC", "KES 8,385,000", "+1.8%", true, Color(0xFFF7931A)),
        MarketItem("Ethereum", "ETH", "KES 451,500", "+0.9%", true, Color(0xFF627EEA)),
        MarketItem("Tether", "USDT", "KES 129.40", "-0.1%", false, Color(0xFF26A17B)),
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            DashboardHeader(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.merchant_greeting, session.agentName),
                onSettings = onSettings,
            )
        }
        item { PortfolioPanel() }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DashboardSectionHeader(
                    title = stringResource(R.string.quick_actions),
                    supportingText = stringResource(R.string.quick_actions_hint),
                )
                PrimaryAction(
                    icon = Icons.Filled.CreditCard,
                    title = stringResource(R.string.accept_payment),
                    subtitle = stringResource(R.string.accept_payment_hint),
                    onClick = onCharge,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SecondaryAction(
                        icon = Icons.Filled.Contactless,
                        label = stringResource(R.string.tap_card),
                        onClick = onTapCard,
                        modifier = Modifier.weight(1f),
                    )
                    SecondaryAction(
                        icon = Icons.Filled.History,
                        label = stringResource(R.string.history),
                        onClick = onHistory,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        item {
            DashboardSectionHeader(
                title = stringResource(R.string.recent_activity),
                supportingText = stringResource(R.string.recent_activity_hint),
                actionLabel = stringResource(R.string.view_all),
                onAction = onHistory,
            )
        }
        items(recentPayments, key = RecentPayment::subtitle) { payment ->
            RecentPaymentRow(payment, onClick = onHistory)
        }
        item {
            DashboardSectionHeader(
                title = stringResource(R.string.market_snapshot),
                supportingText = stringResource(R.string.market_snapshot_hint),
            )
        }
        items(markets, key = MarketItem::symbol) { market -> MarketRow(market) }
    }
}

@Composable
private fun PortfolioPanel() {
    DashboardInkPanel {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.portfolio_value),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.68f),
            )
            DashboardStatusBadge(stringResource(R.string.terminal_online))
        }
        Text(
            stringResource(R.string.portfolio_amount),
            modifier = Modifier.padding(top = 18.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.today), color = Color.White.copy(alpha = 0.56f), style = MaterialTheme.typography.bodySmall)
                Row(modifier = Modifier.padding(top = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.TrendingUp, null, Modifier.size(17.dp), tint = Color(0xFF38D39F))
                    Text(
                        stringResource(R.string.portfolio_change),
                        modifier = Modifier.padding(start = 5.dp),
                        color = Color(0xFF38D39F),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Sparkline(Modifier.width(100.dp).height(38.dp))
        }
    }
}

@Composable
private fun Sparkline(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val values = listOf(0.72f, 0.58f, 0.66f, 0.38f, 0.46f, 0.2f, 0.28f, 0.06f)
        val step = size.width / (values.size - 1)
        val path = Path()
        values.forEachIndexed { index, value ->
            val point = Offset(step * index, size.height * value)
            if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
        }
        drawPath(path, Color(0xFF38D39F), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        val last = Offset(size.width, size.height * values.last())
        drawCircle(Color(0xFF38D39F), radius = 4.dp.toPx(), center = last)
    }
}

@Composable
private fun PrimaryAction(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(76.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f),
            ) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null) }
            }
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.68f))
            }
            Icon(Icons.Filled.ChevronRight, null)
        }
    }
}

@Composable
private fun SecondaryAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(66.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Text(label, modifier = Modifier.weight(1f).padding(start = 10.dp), fontWeight = FontWeight.Bold)
            Icon(Icons.Filled.ChevronRight, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun RecentPaymentRow(payment: RecentPayment, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.ReceiptLong, null, tint = MaterialTheme.colorScheme.secondary)
            }
        }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(payment.title, fontWeight = FontWeight.SemiBold)
            Text(payment.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(payment.amount, fontWeight = FontWeight.Bold, color = Color(0xFF087A55))
    }
}

@Composable
private fun MarketRow(item: MarketItem) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(42.dp), shape = CircleShape, color = item.color.copy(alpha = 0.14f)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.CurrencyBitcoin, null, tint = item.color)
            }
        }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(item.name, fontWeight = FontWeight.SemiBold)
            Text(item.symbol, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(item.price, fontWeight = FontWeight.SemiBold)
            Text(
                item.movement,
                style = MaterialTheme.typography.bodySmall,
                color = if (item.positive) Color(0xFF087A55) else MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun WalletOverview(
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current
    val assets = listOf(
        WalletAsset("Bitcoin", "BTC", "0.15", "KES 9,750.00", 0.58f, Color(0xFFF7931A)),
        WalletAsset("Ethereum", "ETH", "1.2", "KES 4,200.00", 0.25f, Color(0xFF627EEA)),
        WalletAsset("Tether", "USDT", "500.00", "KES 500.00", 0.10f, Color(0xFF26A17B)),
        WalletAsset("Solana", "SOL", "15.5", "KES 2,247.50", 0.07f, Color(0xFF7C4DFF)),
    )
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            DashboardHeader(
                title = stringResource(R.string.wallet),
                subtitle = stringResource(R.string.wallet_subtitle),
                onSettings = onSettings,
            )
        }
        item {
            DashboardInkPanel {
                Text(stringResource(R.string.total_balance), color = Color.White.copy(alpha = 0.66f), style = MaterialTheme.typography.labelLarge)
                Text(
                    stringResource(R.string.wallet_total),
                    modifier = Modifier.padding(top = 10.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row(Modifier.padding(top = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    DashboardStatusBadge(stringResource(R.string.wallet_ready))
                    Text(
                        stringResource(R.string.asset_count, assets.size),
                        modifier = Modifier.padding(start = 10.dp),
                        color = Color.White.copy(alpha = 0.58f),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccountBalanceWallet, null, tint = MaterialTheme.colorScheme.primary)
                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text(stringResource(R.string.settlement_wallet), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            BuildConfig.MERCHANT_WALLET_ADDRESS,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(BuildConfig.MERCHANT_WALLET_ADDRESS))
                            Toast.makeText(context, context.getString(R.string.wallet_address_copied), Toast.LENGTH_SHORT).show()
                        },
                    ) {
                        Icon(Icons.Filled.ContentCopy, stringResource(R.string.copy_address))
                    }
                }
            }
        }
        item {
            DashboardSectionHeader(
                title = stringResource(R.string.your_assets),
                supportingText = stringResource(R.string.your_assets_hint),
            )
        }
        items(assets, key = WalletAsset::symbol) { asset ->
            WalletAssetRow(asset)
            HorizontalDivider(Modifier.padding(top = 14.dp))
        }
    }
}

@Composable
private fun WalletAssetRow(asset: WalletAsset) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(42.dp), shape = RoundedCornerShape(8.dp), color = asset.color.copy(alpha = 0.14f)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(asset.symbol.take(1), fontWeight = FontWeight.Bold, color = asset.color)
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(asset.name, fontWeight = FontWeight.SemiBold)
                Text("${asset.balance} ${asset.symbol}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(asset.fiat, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { asset.allocation },
            modifier = Modifier.fillMaxWidth().padding(start = 54.dp, top = 10.dp).height(4.dp),
            color = asset.color,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            strokeCap = StrokeCap.Round,
        )
    }
}
