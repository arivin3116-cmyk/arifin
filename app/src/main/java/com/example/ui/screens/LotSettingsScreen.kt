package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConfigAppliedStatus
import com.example.data.model.ConnectionStatus
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.SuperBrainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LotSettingsScreen(
    viewModel: SuperBrainViewModel
) {
    val terminal by viewModel.selectedTerminal.collectAsState()
    val activeConfig by viewModel.activeConfig.collectAsState()

    // Form states initialized with activeConfig values
    var baseLotStr by remember(activeConfig) { mutableStateOf(activeConfig.baseLot.toString()) }
    var lotStepCountStr by remember(activeConfig) { mutableStateOf(activeConfig.lotStepCount.toString()) }
    var lotStepAddStr by remember(activeConfig) { mutableStateOf(activeConfig.lotStepAdd.toString()) }
    var maxPositionsStr by remember(activeConfig) { mutableStateOf(activeConfig.maxPositions.toString()) }
    var minGridDistanceStr by remember(activeConfig) { mutableStateOf(activeConfig.minGridDistancePoints.toString()) }
    var recoveryLotMultStr by remember(activeConfig) { mutableStateOf(activeConfig.recoveryLotMult.toString()) }

    var minTPStr by remember(activeConfig) { mutableStateOf(activeConfig.minTPPoints.toString()) }
    var maxSpreadStr by remember(activeConfig) { mutableStateOf(activeConfig.maxSpreadPoints.toString()) }
    var maxDrawdownStr by remember(activeConfig) { mutableStateOf(activeConfig.maxDrawdownPct.toString()) }
    var dailyTargetProfitStr by remember(activeConfig) { mutableStateOf(activeConfig.dailyTargetProfit.toInt().toString()) }
    var slippageStr by remember(activeConfig) { mutableStateOf(activeConfig.slippageDeviation.toString()) }

    var showConfirmSaveDialog by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Header & Live Sync Status Card
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
                                text = "PENGATURAN LOT EA",
                                color = GoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Modul Utama Kontrol Ukuran Transaksi & Proteksi",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Version Pill
                        Text(
                            text = "Config v${activeConfig.version}",
                            color = GoldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldPrimary.copy(alpha = 0.15f))
                                .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = ObsidianBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Penerapan EA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (statusColor, statusBg, statusIcon) = when (activeConfig.appliedStatus) {
                            ConfigAppliedStatus.APPLIED -> Triple(EmeraldProfit, EmeraldBg, Icons.Default.CheckCircle)
                            ConfigAppliedStatus.PENDING -> Triple(AmberWarning, AmberBg, Icons.Default.Sync)
                            ConfigAppliedStatus.FAILED -> Triple(RubyLoss, RubyBg, Icons.Default.Error)
                            ConfigAppliedStatus.OFFLINE -> Triple(TextMuted, ObsidianSurfaceElevated, Icons.Default.CloudOff)
                        }

                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Status: ${activeConfig.appliedStatus.label}",
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activeConfig.appliedMessage,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    val updateFmt = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(Date(activeConfig.updatedAt))
                    Text(
                        text = "Terakhir diubah: $updateFmt",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Section A: Pengaturan Lot
        item {
            LuxurySectionHeader(
                title = "A. Pengaturan Lot & Layer Multiplier",
                subtitle = "Menyesuaikan volume order dan penambahan bertingkat marti/grid"
            )
            Spacer(modifier = Modifier.height(10.dp))

            ConfigInputField(
                label = "Lot Awal (Base Lot)",
                paramName = "InpBaseLot",
                value = baseLotStr,
                onValueChange = { baseLotStr = it },
                tooltip = "Ukuran volume order pertama saat entry awal. Default: 0.01",
                placeholder = "0.01",
                keyboardType = KeyboardType.Decimal
            )

            ConfigInputField(
                label = "Jumlah Order Kenaikan Lot (Step Count)",
                paramName = "InpLotStepCount",
                value = lotStepCountStr,
                onValueChange = { lotStepCountStr = it },
                tooltip = "Setiap kelipatan X order, lot akan dinaikkan sesuai InpLotStepAdd. Default: 1",
                placeholder = "1",
                keyboardType = KeyboardType.Number
            )

            ConfigInputField(
                label = "Penambahan Lot Tiap Step",
                paramName = "InpLotStepAdd",
                value = lotStepAddStr,
                onValueChange = { lotStepAddStr = it },
                tooltip = "Nilai lot yang ditambahkan pada setiap step kenaikan. Default: 0.1",
                placeholder = "0.1",
                keyboardType = KeyboardType.Decimal
            )

            ConfigInputField(
                label = "Batas Maksimal Posisi (Anti-Numpuk)",
                paramName = "InpMaxPositions",
                value = maxPositionsStr,
                onValueChange = { maxPositionsStr = it },
                tooltip = "Batas maksimal akumulasi posisi terbuka pada simbol ini. Default: 20",
                placeholder = "20",
                keyboardType = KeyboardType.Number
            )

            ConfigInputField(
                label = "Jarak Minimal Antar Layer (Points)",
                paramName = "InpMinGridDistancePoints",
                value = minGridDistanceStr,
                onValueChange = { minGridDistanceStr = it },
                tooltip = "Jarak lantai minimal antar-layer grid dalam poin harga emas. Default: 30",
                placeholder = "30",
                keyboardType = KeyboardType.Number
            )

            ConfigInputField(
                label = "Lot Recovery Multiplier (Breakout)",
                paramName = "InpRecoveryLotMult",
                value = recoveryLotMultStr,
                onValueChange = { recoveryLotMultStr = it },
                tooltip = "Faktor pengali lot hedging saat terjadi breakout support/resistance. Default: 1.5",
                placeholder = "1.5",
                keyboardType = KeyboardType.Decimal
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Section B: Target dan Proteksi
        item {
            LuxurySectionHeader(
                title = "B. Target Profit & Proteksi Risiko",
                subtitle = "Batas toleransi spread, target keranjang, dan hard cut loss"
            )
            Spacer(modifier = Modifier.height(10.dp))

            ConfigInputField(
                label = "Target Profit Minimal per 0.01 Lot (Points)",
                paramName = "InpMinTP_Points",
                value = minTPStr,
                onValueChange = { minTPStr = it },
                tooltip = "Target profit keranjang akumulasi per 0.01 volume dalam poin. Default: 10",
                placeholder = "10",
                keyboardType = KeyboardType.Number
            )

            ConfigInputField(
                label = "Batas Spread Maksimum (Points)",
                paramName = "InpMaxSpreadPoints",
                value = maxSpreadStr,
                onValueChange = { maxSpreadStr = it },
                tooltip = "EA menolak membuka entry baru jika spread broker melebihi batas ini. Default: 35",
                placeholder = "35",
                keyboardType = KeyboardType.Number
            )

            ConfigInputField(
                label = "Maksimal Drawdown Hard Cut Loss (%)",
                paramName = "InpMaxDrawdownPct",
                value = maxDrawdownStr,
                onValueChange = { maxDrawdownStr = it },
                tooltip = "Persentase kerugian equity maksimum sebelum emergency Close All diaktifkan otomatis. Default: 20%",
                placeholder = "20",
                keyboardType = KeyboardType.Decimal
            )

            ConfigInputField(
                label = "Target Profit Harian ($ USD)",
                paramName = "InpDailyTargetProfit",
                value = dailyTargetProfitStr,
                onValueChange = { dailyTargetProfitStr = it },
                tooltip = "Lock target keuntungan harian. Jika tercapai, EA otomatis berhenti untuk hari ini. Default: 99999",
                placeholder = "99999",
                keyboardType = KeyboardType.Number
            )

            ConfigInputField(
                label = "Slippage Deviasi Maksimum (Points)",
                paramName = "InpSlippageDeviation",
                value = slippageStr,
                onValueChange = { slippageStr = it },
                tooltip = "Toleransi pergeseran harga eksekusi order pada broker. Default: 30",
                placeholder = "30",
                keyboardType = KeyboardType.Number
            )
        }

        // Error Banner
        if (validationError != null) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = RubyBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RubyLoss.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = RubyLoss, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = validationError ?: "", color = RubyLoss, fontSize = 12.sp)
                    }
                }
            }
        }

        // Action Save Button
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    // Validate
                    val bLot = baseLotStr.toDoubleOrNull()
                    val sCount = lotStepCountStr.toIntOrNull()
                    val sAdd = lotStepAddStr.toDoubleOrNull()
                    val mPos = maxPositionsStr.toIntOrNull()
                    val mGrid = minGridDistanceStr.toIntOrNull()
                    val rMult = recoveryLotMultStr.toDoubleOrNull()
                    val mTP = minTPStr.toIntOrNull()
                    val mSpread = maxSpreadStr.toDoubleOrNull()
                    val mDD = maxDrawdownStr.toDoubleOrNull()
                    val dTP = dailyTargetProfitStr.toDoubleOrNull()
                    val slip = slippageStr.toLongOrNull()

                    if (bLot == null || bLot <= 0.0 || bLot > 5.0) {
                        validationError = "Base Lot harus bernilai positif antara 0.01 - 5.00!"
                        return@Button
                    }
                    if (sCount == null || sCount < 1) {
                        validationError = "Step Count minimal 1 order!"
                        return@Button
                    }
                    if (sAdd == null || sAdd < 0.0) {
                        validationError = "Penambahan lot tidak boleh bernilai negatif!"
                        return@Button
                    }
                    if (mPos == null || mPos < 1 || mPos > 100) {
                        validationError = "Maksimal posisi harus antara 1 - 100!"
                        return@Button
                    }
                    if (mGrid == null || mGrid < 5) {
                        validationError = "Jarak grid minimal adalah 5 points!"
                        return@Button
                    }
                    if (mTP == null || mTP < 1) {
                        validationError = "Min TP minimal 1 point!"
                        return@Button
                    }

                    validationError = null
                    showConfirmSaveDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_lot_settings_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF1E1500), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SIMPAN PERUBAHAN & SINKRONKAN EA",
                    color = Color(0xFF1E1500),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Confirmation Dialog
    if (showConfirmSaveDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmSaveDialog = false },
            containerColor = ObsidianSurface,
            title = {
                Text("Konfirmasi Perubahan Parameter", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text(
                        text = "Konfigurasi akan dinaikkan ke versi v${activeConfig.version + 1}. EA di VPS akan membaca perubahan pada heartbeat berikutnya tanpa perlu restart.",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Base Lot: $baseLotStr\n• Max Posisi: $maxPositionsStr\n• Min TP: $minTPStr points\n• Jarak Grid: $minGridDistanceStr points",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmSaveDialog = false
                        viewModel.saveLotSettings(
                            baseLot = baseLotStr.toDouble(),
                            lotStepCount = lotStepCountStr.toInt(),
                            lotStepAdd = lotStepAddStr.toDouble(),
                            maxPositions = maxPositionsStr.toInt(),
                            minGridDistancePoints = minGridDistanceStr.toInt(),
                            recoveryLotMult = recoveryLotMultStr.toDouble(),
                            minTPPoints = minTPStr.toInt(),
                            maxSpreadPoints = maxSpreadStr.toDouble(),
                            maxDrawdownPct = maxDrawdownStr.toDouble(),
                            dailyTargetProfit = dailyTargetProfitStr.toDouble(),
                            slippageDeviation = slippageStr.toLong()
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Ya, Terapkan Sekarang", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmSaveDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun ConfigInputField(
    label: String,
    paramName: String,
    value: String,
    onValueChange: (String) -> Unit,
    tooltip: String,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = paramName,
                    color = GoldLight,
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ObsidianSurfaceElevated)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tooltip,
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = TextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = ObsidianBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = GoldPrimary,
                    focusedContainerColor = ObsidianSurfaceElevated,
                    unfocusedContainerColor = ObsidianSurfaceElevated
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_$paramName")
            )
        }
    }
}
