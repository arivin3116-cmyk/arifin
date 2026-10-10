package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SuperBrainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: SuperBrainViewModel,
    onNavigate: (ScreenDestination) -> Unit
) {
    val terminal by viewModel.selectedTerminal.collectAsState()
    val config by viewModel.activeConfig.collectAsState()
    val equityPoints = viewModel.equityHistory

    if (terminal == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Belum Ada Terminal MT5 Terhubung",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Daftarkan terminal VPS Anda untuk mulai memantau dan mengontrol EA.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { onNavigate(ScreenDestination.TERMINALS) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    modifier = Modifier.testTag("add_first_terminal_button")
                ) {
                    Text("+ Daftarkan Terminal Pertama", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val term = terminal!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Connection & Status Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, if (term.connectionStatus == ConnectionStatus.ONLINE) ObsidianBorderGold else RubyLoss.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = term.name,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${term.eaName} v${term.eaVersion} • Magic: ${config.magicNumber}",
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        StatusPill(connectionStatus = term.connectionStatus)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = ObsidianBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "BROKER & AKUN", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${term.broker} (#${term.accountNumber})", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "SIMBOL & TIMEFRAME", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = term.symbol,
                                    color = GoldLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = term.timeframe,
                                    color = CyanAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyanAccent.copy(alpha = 0.15f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TradingStatusBadge(tradingStatus = term.tradingStatus)

                        val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(term.lastHeartbeat))
                        Text(
                            text = "Heartbeat: $timeFmt (${(System.currentTimeMillis() - term.lastHeartbeat) / 1000}s lalu)",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    // Test toggle connection button for safety testing
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { viewModel.toggleTerminalOnline(term.id) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = if (term.connectionStatus == ConnectionStatus.ONLINE) Icons.Default.WifiOff else Icons.Default.Wifi,
                                contentDescription = null,
                                tint = if (term.connectionStatus == ConnectionStatus.ONLINE) AmberWarning else EmeraldProfit,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (term.connectionStatus == ConnectionStatus.ONLINE) "Simulasi Disconnect" else "Hubungkan Kembali",
                                color = if (term.connectionStatus == ConnectionStatus.ONLINE) AmberWarning else EmeraldProfit,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        // Primary Metrics Grid
        item {
            LuxurySectionHeader(
                title = "Metrik Akun & Performa",
                subtitle = "Sinkronisasi langsung dari VPS MetaTrader 5"
            )
            Spacer(modifier = Modifier.height(10.dp))

            val floatIsPositive = term.floatingProfit >= 0
            val floatColor = if (floatIsPositive) EmeraldProfit else RubyLoss
            val floatSign = if (floatIsPositive && term.floatingProfit > 0) "+" else ""

            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    title = "BALANCE",
                    value = "$${String.format(Locale.US, "%,.2f", term.balance)}",
                    subtitle = "Saldo Dasar Akun",
                    icon = Icons.Default.AccountBalanceWallet,
                    valueColor = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatCard(
                    title = "EQUITY",
                    value = "$${String.format(Locale.US, "%,.2f", term.equity)}",
                    subtitle = "Nilai Ekuitas Aktif",
                    icon = Icons.Default.TrendingUp,
                    valueColor = GoldLight,
                    badgeText = "${((term.equity - term.balance) / term.balance * 100).let { String.format(Locale.US, "%+.1f%%", it) }}",
                    badgeColor = if (term.equity >= term.balance) EmeraldProfit else RubyLoss,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    title = "FLOATING P/L",
                    value = "$floatSign$${String.format(Locale.US, "%,.2f", term.floatingProfit)}",
                    subtitle = "${term.activePositions} Posisi Berjalan",
                    icon = Icons.Default.MonetizationOn,
                    valueColor = floatColor,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                StatCard(
                    title = "PROFIT HARI INI",
                    value = "+$${String.format(Locale.US, "%,.2f", term.todayProfit)}",
                    subtitle = "Target: $${config.dailyTargetProfit.toInt()}",
                    icon = Icons.Default.AttachMoney,
                    valueColor = EmeraldProfit,
                    badgeText = "LOCK ON TARGET",
                    badgeColor = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        // Live Order Matrix
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MATRIKS POSISI & SPREAD",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("BUY", color = EmeraldProfit, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${term.buyPositions}", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Order", color = TextMuted, fontSize = 10.sp)
                        }
                        Box(modifier = Modifier.width(1.dp).height(38.dp).background(ObsidianBorder))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SELL", color = RubyLoss, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${term.sellPositions}", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Order", color = TextMuted, fontSize = 10.sp)
                        }
                        Box(modifier = Modifier.width(1.dp).height(38.dp).background(ObsidianBorder))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TOTAL LOT", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(String.format(Locale.US, "%.2f", term.totalLot), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Lot", color = TextMuted, fontSize = 10.sp)
                        }
                        Box(modifier = Modifier.width(1.dp).height(38.dp).background(ObsidianBorder))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SPREAD", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("${term.spread.toInt()}", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Points", color = TextMuted, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = ObsidianBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Layer progress bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Penggunaan Batas Layer Posisi",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${term.activePositions} / ${term.maxPositionsLimit} Maks (${(term.activePositions.toFloat() / term.maxPositionsLimit * 100).toInt()}%)",
                            color = GoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    val progress = (term.activePositions.toFloat() / term.maxPositionsLimit).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (progress > 0.7f) RubyLoss else GoldPrimary,
                        trackColor = ObsidianSurfaceElevated
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        // Equity Chart Curve
        item {
            LuxurySectionHeader(
                title = "Grafik Pertumbuhan Equity",
                subtitle = "Riwayat pergerakan ekuitas sesi trading hari ini",
                tag = "REALTIME"
            )
            Spacer(modifier = Modifier.height(8.dp))

            SimpleEquityChart(
                points = equityPoints.map { it.timeLabel to it.equity },
                lineColor = EmeraldProfit
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Quick Navigation Shortcuts
        item {
            LuxurySectionHeader(
                title = "Aksi & Konfigurasi Cepat",
                subtitle = "Beralih langsung ke modul pengelolaan EA"
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onNavigate(ScreenDestination.LOT_SETTINGS) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_nav_lot_button")
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFF1E1500), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pengaturan Lot", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onNavigate(ScreenDestination.CONTROL_CENTER) },
                    colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("dashboard_nav_control_button")
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pusat Kendali", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { onNavigate(ScreenDestination.MQL5_INTEGRATION) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanAccent),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lihat Kode MQL5 & Panduan WebRequest VPS", fontSize = 12.sp)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
