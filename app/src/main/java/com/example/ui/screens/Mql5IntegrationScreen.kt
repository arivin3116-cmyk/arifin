package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.mql5.Mql5Source
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.SuperBrainViewModel

@Composable
fun Mql5IntegrationScreen(
    viewModel: SuperBrainViewModel
) {
    val context = LocalContext.current
    val mql5Code = Mql5Source.CODE

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "SOURCE CODE EA MQL5 LENGKAP",
                                color = GoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "WorldClass_SuperBrain_Scalper_Cloud.mq5 (v6.40)",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("MQL5 Source Code", mql5Code)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showMessage("Source Code MQL5 berhasil disalin ke clipboard!")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("copy_mql5_code_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF1E1500), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin Kode", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Kode ini mempertahankan 100% algoritma strategi asli (EMA Multi-Timeframe, ATR Progressive Grid, Breakout S/R Recovery) dan mengintegrasikan WebRequest HTTP berkecepatan tinggi dengan variabel runtime dinamis.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Checklist Pengujian Akun Demo
        item {
            LuxurySectionHeader(
                title = "Daftar Pengujian Integrasi (Tahap Demo)",
                subtitle = "Verifikasi alur sebelum menyalakan pada akun real live"
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ChecklistRow("1. Pasang EA pada Akun Demo MT5 broker Anda.")
                    ChecklistRow("2. Pastikan izin URL WebRequest 'https://api.superbrain-ea.com' dicentang.")
                    ChecklistRow("3. Periksa status terminal di Dashboard website berubah menjadi 'ONLINE'.")
                    ChecklistRow("4. Uji perubahan Lot Awal di website: verifikasi configVersion bertambah dan di-ACK oleh EA.")
                    ChecklistRow("5. Uji tombol 'Jeda Entry Baru' dan 'Emergency Close All' untuk memastikan respon instan terminal.")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Code Viewer Preview
        item {
            LuxurySectionHeader(
                title = "Preview File MQL5 (Siap Kompilasi)",
                subtitle = "Dapat langsung dicopy dan dikompilasi di MetaEditor 5 VPS"
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianBackground)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = mql5Code,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun ChecklistRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(GoldPrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = TextPrimary, fontSize = 12.sp)
    }
}
