package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LogCategory
import com.example.data.model.LogSeverity
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.SuperBrainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConfigHistoryScreen(
    viewModel: SuperBrainViewModel
) {
    val auditLogs by viewModel.auditLogs.collectAsState()
    val activeConfig by viewModel.activeConfig.collectAsState()

    var selectedFilter by remember { mutableStateOf<LogCategory?>(null) }

    val filteredLogs = remember(auditLogs, selectedFilter) {
        if (selectedFilter == null) auditLogs else auditLogs.filter { it.category == selectedFilter }
    }

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
                    .border(1.dp, ObsidianBorderGold, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "RIWAYAT KONFIGURASI & AUDIT LOG",
                        color = GoldPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Setiap perubahan parameter dicatat dengan stempel waktu, nomor versi unik (Config v${activeConfig.version}), dan status konfirmasi eksekusi dari VPS.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        // Filter Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("Semua Log") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = GoldLight
                    )
                )
                FilterChip(
                    selected = selectedFilter == LogCategory.CONFIG,
                    onClick = { selectedFilter = LogCategory.CONFIG },
                    label = { Text("Konfigurasi") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = GoldLight
                    )
                )
                FilterChip(
                    selected = selectedFilter == LogCategory.COMMAND,
                    onClick = { selectedFilter = LogCategory.COMMAND },
                    label = { Text("Perintah") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = GoldLight
                    )
                )
                FilterChip(
                    selected = selectedFilter == LogCategory.SYSTEM,
                    onClick = { selectedFilter = LogCategory.SYSTEM },
                    label = { Text("Sistem") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = GoldLight
                    )
                )
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        // Logs list
        item {
            LuxurySectionHeader(
                title = "Entri Audit (${filteredLogs.size})",
                subtitle = "Transparansi penuh riwayat komunikasi antarmuka web dan terminal"
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(filteredLogs) { log ->
            val (badgeColor, badgeBg) = when (log.severity) {
                LogSeverity.SUCCESS -> Pair(EmeraldProfit, EmeraldBg)
                LogSeverity.INFO -> Pair(CyanAccent, CyanAccent.copy(alpha = 0.15f))
                LogSeverity.WARNING -> Pair(AmberWarning, AmberBg)
                LogSeverity.DANGER -> Pair(RubyLoss, RubyBg)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = log.category.label.uppercase(),
                                color = badgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(badgeBg)
                                    .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = log.title,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val dateFmt = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                        Text(text = dateFmt, color = TextMuted, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = log.description,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
