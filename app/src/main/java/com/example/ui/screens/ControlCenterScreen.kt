package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.EmergencyCloseAllDialog
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.components.TradingStatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.SuperBrainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ControlCenterScreen(
    viewModel: SuperBrainViewModel
) {
    val terminal by viewModel.selectedTerminal.collectAsState()
    val commands by viewModel.commands.collectAsState()
    val showCloseAllDialog by viewModel.showCloseAllDialog.collectAsState()

    val term = terminal ?: return

    val isPaused = term.tradingStatus == TradingStatus.PAUSED

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorderGold, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PUSAT KENDALI EKSEKUSI EA",
                                color = GoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Terminal: ${term.name}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        TradingStatusBadge(tradingStatus = term.tradingStatus)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Kontrol cloud langsung terhubung dengan antrean WebRequest di terminal MT5. Perintah darurat diaudit dan dikonfirmasi dengan status real-time.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Standard Operational Controls
        item {
            LuxurySectionHeader(
                title = "1. Kontrol Operasional Trading",
                subtitle = "Pengelolaan alur pembukaan posisi baru tanpa menutup posisi yang sedang floating"
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Pause / Resume Toggle
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isPaused) AmberBg else EmeraldBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPaused) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (isPaused) AmberWarning else EmeraldProfit,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isPaused) "Trading Sedang Dijeda" else "Trading Berjalan Aktif",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isPaused) "EA tidak akan membuka order baru" else "EA membuka order saat sinyal EMA/ATR valid",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (isPaused) {
                                    viewModel.executeCommand(CommandType.RESUME_TRADING)
                                } else {
                                    viewModel.executeCommand(CommandType.PAUSE_NEW_ENTRIES)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPaused) EmeraldProfit else AmberWarning
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("toggle_pause_resume_button")
                        ) {
                            Text(
                                text = if (isPaused) "Lanjutkan" else "Jeda Entry",
                                color = if (isPaused) Color(0xFF002111) else Color(0xFF2C1600),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated)
                    ) {
                        Text(
                            text = "CATATAN: Menjeda entry baru TIDAK akan menutup posisi yang sedang floating. Posisi terbuka akan tetap dikelola oleh algoritma TP keranjang dan Grid EA.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sync Config Command
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Paksa Sinkronisasi Config", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Kirim ulang seluruh tabel parameter ke EA", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.executeCommand(CommandType.FORCE_SYNC_CONFIG) },
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("force_sync_config_button")
                    ) {
                        Text("Sinkronkan", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // High Risk Emergency Controls
        item {
            LuxurySectionHeader(
                title = "2. Perintah Darurat Risiko Tinggi",
                subtitle = "Intervensi paksa penutupan pasar dengan konfirmasi berlapis",
                tag = "HIGH RISK"
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RubyLoss.copy(alpha = 0.6f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = RubyBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RubyLoss.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = RubyLoss, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TUTUP SELURUH POSISI (CLOSE ALL)",
                                color = RubyLoss,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Emergency kill-switch pada simbol ${term.symbol}",
                                color = TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Perintah ini akan menginstruksikan EA untuk melakukan perulangan penutupan instan hingga seluruh order BUY dan SELL pada Magic Number EA tertutup tuntas.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.openCloseAllDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = RubyLoss),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("emergency_close_all_trigger_button")
                    ) {
                        Icon(Icons.Default.Dangerous, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("EKSEKUSI CLOSE ALL DARURAT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Commands Queue & Audit Trail
        item {
            LuxurySectionHeader(
                title = "3. Antrean & Riwayat Eksekusi Perintah",
                subtitle = "Pelacakan status: Dalam Antrean -> Terkirim -> Dikonfirmasi Terminal"
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (commands.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Text(
                        text = "Belum ada riwayat perintah kendali yang dikirim.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(commands) { cmd ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = cmd.type.label,
                                    color = if (cmd.type.isHighRisk) RubyLoss else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            val dateFmt = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault()).format(Date(cmd.createdAt))
                            Text(text = "Dikirim: $dateFmt", color = TextMuted, fontSize = 11.sp)
                            if (cmd.resultMessage != null) {
                                Text(
                                    text = "Hasil: ${cmd.resultMessage}",
                                    color = EmeraldProfit,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        val (statusColor, statusBg) = when (cmd.status) {
                            CommandStatus.ACKNOWLEDGED -> Pair(EmeraldProfit, EmeraldBg)
                            CommandStatus.SENT -> Pair(CyanAccent, CyanAccent.copy(alpha = 0.15f))
                            CommandStatus.QUEUED -> Pair(AmberWarning, AmberBg)
                            CommandStatus.FAILED -> Pair(RubyLoss, RubyBg)
                        }

                        Text(
                            text = cmd.status.label,
                            color = statusColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusBg)
                                .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Emergency Close All Dialog
    EmergencyCloseAllDialog(
        isOpen = showCloseAllDialog,
        onConfirm = { viewModel.executeEmergencyCloseAll() },
        onDismiss = { viewModel.closeCloseAllDialog() }
    )
}
