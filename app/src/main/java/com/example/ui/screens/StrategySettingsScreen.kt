package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Save
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
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.SuperBrainViewModel

@Composable
fun StrategySettingsScreen(
    viewModel: SuperBrainViewModel
) {
    val activeConfig by viewModel.activeConfig.collectAsState()

    // EMA Filter states
    var useEMAFilter by remember(activeConfig) { mutableStateOf(activeConfig.useEMAFilter) }
    var emaM5Str by remember(activeConfig) { mutableStateOf(activeConfig.emaPeriodM5.toString()) }
    var useEMAM15 by remember(activeConfig) { mutableStateOf(activeConfig.useEMAM15) }
    var emaM15Str by remember(activeConfig) { mutableStateOf(activeConfig.emaPeriodM15.toString()) }
    var useEMAH1 by remember(activeConfig) { mutableStateOf(activeConfig.useEMAH1) }
    var emaH1Str by remember(activeConfig) { mutableStateOf(activeConfig.emaPeriodH1.toString()) }

    // ATR Progressive Grid states
    var atrPeriodStr by remember(activeConfig) { mutableStateOf(activeConfig.atrPeriod.toString()) }
    var atrMultStep1Str by remember(activeConfig) { mutableStateOf(activeConfig.atrMultStep1.toString()) }
    var atrMultStep2Str by remember(activeConfig) { mutableStateOf(activeConfig.atrMultStep2.toString()) }
    var atrMultStep3Str by remember(activeConfig) { mutableStateOf(activeConfig.atrMultStep3.toString()) }
    var atrMultIncStr by remember(activeConfig) { mutableStateOf(activeConfig.atrMultInc.toString()) }

    // Breakout Support / Resistance states
    var useBreakoutRecovery by remember(activeConfig) { mutableStateOf(activeConfig.useBreakoutRecovery) }
    var srPeriodStr by remember(activeConfig) { mutableStateOf(activeConfig.srPeriod.toString()) }

    // Time filter states
    var useTimeFilter by remember(activeConfig) { mutableStateOf(activeConfig.useTimeFilter) }
    var startHourStr by remember(activeConfig) { mutableStateOf(activeConfig.startHour.toString()) }
    var endHourStr by remember(activeConfig) { mutableStateOf(activeConfig.endHour.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp)),
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
                                text = "OTAK STRATEGI MULTI-TIMEFRAME",
                                color = GoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Filter Tren EMA, Grid Dinamis ATR, & Hedging Breakout",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Icon(Icons.Default.AutoGraph, contentDescription = null, tint = CyanAccent)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 1. EMA Multi-Timeframe Trend Filter
        item {
            LuxurySectionHeader(
                title = "1. Filter Tren Multi-Timeframe (EMA)",
                subtitle = "Menyaring arah pasar berdasarkan pergerakan M5, M15, dan H1"
            )
            Spacer(modifier = Modifier.height(10.dp))

            StrategyToggleCard(
                title = "Aktifkan Filter Tren EMA",
                paramName = "InpUseEMAFilter",
                description = "Jika aktif, EA hanya membuka order searah dengan tren EMA M5",
                isChecked = useEMAFilter,
                onCheckedChange = { useEMAFilter = it }
            )

            ConfigInputField(
                label = "Periode EMA M5 (Tren Pendek)",
                paramName = "InpEMAPeriodM5",
                value = emaM5Str,
                onValueChange = { emaM5Str = it },
                tooltip = "Periode garis EMA pada timeframe M5. Default: 50",
                placeholder = "50",
                keyboardType = KeyboardType.Number
            )

            StrategyToggleCard(
                title = "Konfirmasi EMA M15 (Tren Menengah)",
                paramName = "InpUseEMAM15",
                description = "Konfirmasi tren searah dengan EMA timeframe M15",
                isChecked = useEMAM15,
                onCheckedChange = { useEMAM15 = it }
            )

            ConfigInputField(
                label = "Periode EMA M15",
                paramName = "InpEMAPeriodM15",
                value = emaM15Str,
                onValueChange = { emaM15Str = it },
                tooltip = "Periode garis EMA pada timeframe M15. Default: 50",
                placeholder = "50",
                keyboardType = KeyboardType.Number
            )

            StrategyToggleCard(
                title = "Konfirmasi EMA H1 (Tren Utama)",
                paramName = "InpUseEMAH1",
                description = "Konfirmasi tren searah dengan EMA timeframe H1",
                isChecked = useEMAH1,
                onCheckedChange = { useEMAH1 = it }
            )

            ConfigInputField(
                label = "Periode EMA H1",
                paramName = "InpEMAPeriodH1",
                value = emaH1Str,
                onValueChange = { emaH1Str = it },
                tooltip = "Periode garis EMA pada timeframe H1. Default: 50",
                placeholder = "50",
                keyboardType = KeyboardType.Number
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 2. ATR Progressive Grid Settings
        item {
            LuxurySectionHeader(
                title = "2. ATR Progressive Grid",
                subtitle = "Jarak averaging dinamis menyesuaikan volatilitas pasar emas M1"
            )
            Spacer(modifier = Modifier.height(10.dp))

            ConfigInputField(
                label = "Periode ATR M1",
                paramName = "InpATRPeriod",
                value = atrPeriodStr,
                onValueChange = { atrPeriodStr = it },
                tooltip = "Periode kalkulasi Average True Range pada timeframe M1. Default: 14",
                placeholder = "14",
                keyboardType = KeyboardType.Number
            )

            ConfigInputField(
                label = "Multiplier ATR Layer 1 (Order ke-2)",
                paramName = "InpATRMultStep1",
                value = atrMultStep1Str,
                onValueChange = { atrMultStep1Str = it },
                tooltip = "Pengali jarak ATR untuk layer kedua. Default: 0.2",
                placeholder = "0.2",
                keyboardType = KeyboardType.Decimal
            )

            ConfigInputField(
                label = "Multiplier ATR Layer 2 (Order ke-3)",
                paramName = "InpATRMultStep2",
                value = atrMultStep2Str,
                onValueChange = { atrMultStep2Str = it },
                tooltip = "Pengali jarak ATR untuk layer ketiga. Default: 0.5",
                placeholder = "0.5",
                keyboardType = KeyboardType.Decimal
            )

            ConfigInputField(
                label = "Multiplier ATR Layer 3 (Order ke-4)",
                paramName = "InpATRMultStep3",
                value = atrMultStep3Str,
                onValueChange = { atrMultStep3Str = it },
                tooltip = "Pengali jarak ATR untuk layer keempat. Default: 1.0",
                placeholder = "1.0",
                keyboardType = KeyboardType.Decimal
            )

            ConfigInputField(
                label = "Penambahan Multiplier per Layer Berikutnya",
                paramName = "InpATRMultInc",
                value = atrMultIncStr,
                onValueChange = { atrMultIncStr = it },
                tooltip = "Tambahan kenaikan multiplier jarak untuk layer di atas 4. Default: 0.5",
                placeholder = "0.5",
                keyboardType = KeyboardType.Decimal
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 3. Breakout S/R Recovery
        item {
            LuxurySectionHeader(
                title = "3. Otak Breakout Support/Resistance",
                subtitle = "Membuka order counter-trend hedging ketika terjadi penembusan level kunci"
            )
            Spacer(modifier = Modifier.height(10.dp))

            StrategyToggleCard(
                title = "Aktifkan Breakout Recovery M5",
                paramName = "InpUseBreakoutRecovery",
                description = "Mendeteksi breakout candle support/resistance untuk recovery hedging",
                isChecked = useBreakoutRecovery,
                onCheckedChange = { useBreakoutRecovery = it }
            )

            ConfigInputField(
                label = "Periode Candle Support/Resistance (M5)",
                paramName = "InpSRPeriod",
                value = srPeriodStr,
                onValueChange = { srPeriodStr = it },
                tooltip = "Jumlah candle M5 yang diperiksa untuk mencari titik tertinggi/terendah. Default: 20",
                placeholder = "20",
                keyboardType = KeyboardType.Number
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 4. Jam Trading Filter
        item {
            LuxurySectionHeader(
                title = "4. Filter Jam Trading",
                subtitle = "Membatasi waktu operasional entry baru EA berdasarkan jam server MT5"
            )
            Spacer(modifier = Modifier.height(10.dp))

            StrategyToggleCard(
                title = "Aktifkan Filter Jam Trading",
                paramName = "InpUseTimeFilter",
                description = "Jika diaktifkan, EA hanya open entry pada jendela waktu yang ditentukan",
                isChecked = useTimeFilter,
                onCheckedChange = { useTimeFilter = it }
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                ConfigInputField(
                    label = "Jam Mulai (Server)",
                    paramName = "InpStartHour",
                    value = startHourStr,
                    onValueChange = { startHourStr = it },
                    tooltip = "Waktu mulai buka posisi (0-23). Default: 8",
                    placeholder = "8",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ConfigInputField(
                    label = "Jam Selesai (Server)",
                    paramName = "InpEndHour",
                    value = endHourStr,
                    onValueChange = { endHourStr = it },
                    tooltip = "Waktu selesai buka posisi (0-23). Default: 22",
                    placeholder = "22",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Save Button
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    viewModel.saveStrategySettings(
                        useEMAFilter = useEMAFilter,
                        emaPeriodM5 = emaM5Str.toIntOrNull() ?: 50,
                        useEMAM15 = useEMAM15,
                        emaPeriodM15 = emaM15Str.toIntOrNull() ?: 50,
                        useEMAH1 = useEMAH1,
                        emaPeriodH1 = emaH1Str.toIntOrNull() ?: 50,
                        atrPeriod = atrPeriodStr.toIntOrNull() ?: 14,
                        atrMultStep1 = atrMultStep1Str.toDoubleOrNull() ?: 0.2,
                        atrMultStep2 = atrMultStep2Str.toDoubleOrNull() ?: 0.5,
                        atrMultStep3 = atrMultStep3Str.toDoubleOrNull() ?: 1.0,
                        atrMultInc = atrMultIncStr.toDoubleOrNull() ?: 0.5,
                        useBreakoutRecovery = useBreakoutRecovery,
                        srPeriod = srPeriodStr.toIntOrNull() ?: 20,
                        useTimeFilter = useTimeFilter,
                        startHour = startHourStr.toIntOrNull() ?: 8,
                        endHour = endHourStr.toIntOrNull() ?: 22
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_strategy_settings_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF1E1500), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SIMPAN PENGATURAN STRATEGI",
                    color = Color(0xFF1E1500),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StrategyToggleCard(
    title: String,
    paramName: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = paramName,
                        color = GoldLight,
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ObsidianSurfaceElevated)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, color = TextMuted, fontSize = 11.sp)
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = GoldPrimary,
                    checkedTrackColor = GoldDark.copy(alpha = 0.5f),
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = ObsidianSurfaceElevated
                )
            )
        }
    }
}
