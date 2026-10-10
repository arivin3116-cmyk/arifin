package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WorkspacePremium
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
import com.example.data.model.SubscriptionTier
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.SuperBrainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SubscriptionScreen(
    viewModel: SuperBrainViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val activeTier = currentUser?.tier ?: SubscriptionTier.STARTER
    val expiresAt = currentUser?.expiresAt ?: (System.currentTimeMillis() + 30L * 86400000)

    var showPaymentModal by remember { mutableStateOf(false) }
    var selectedTierForCheckout by remember { mutableStateOf<SubscriptionTier?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Active Subscription Status Banner
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
                                text = "PAKET LANGGANAN ANDA",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = activeTier.title,
                                color = GoldPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    val expFmt = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date(expiresAt))
                    Text(
                        text = "Aktif hingga: $expFmt • Batas Terminal: ${activeTier.maxTerminals} Akun MT5",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Status Pembayaran Gateway: Siap Integrasi Webhook (Mode Konfirmasi Manual Aktif)",
                        color = CyanAccent,
                        fontSize = 11.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Tier comparison cards
        item {
            LuxurySectionHeader(
                title = "Pilihan Paket Langganan SaaS",
                subtitle = "Pilih level kapabilitas sesuai portofolio dan jumlah akun VPS trading Anda"
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            SubscriptionCard(
                tier = SubscriptionTier.STARTER,
                isCurrent = activeTier == SubscriptionTier.STARTER,
                onSelect = {
                    selectedTierForCheckout = SubscriptionTier.STARTER
                    showPaymentModal = true
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            SubscriptionCard(
                tier = SubscriptionTier.PRO,
                isCurrent = activeTier == SubscriptionTier.PRO,
                isPopular = true,
                onSelect = {
                    selectedTierForCheckout = SubscriptionTier.PRO
                    showPaymentModal = true
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            SubscriptionCard(
                tier = SubscriptionTier.ELITE,
                isCurrent = activeTier == SubscriptionTier.ELITE,
                onSelect = {
                    selectedTierForCheckout = SubscriptionTier.ELITE
                    showPaymentModal = true
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Payment Security Note
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurfaceElevated)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "STANDAR KEAMANAN PEMBAYARAN",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Platform SuperBrain tidak pernah menyimpan data kartu kredit atau kredensial perbankan Anda. Seluruh transaksi diverifikasi melalui webhook resmi gateway pembayaran terenkripsi TLS 1.3.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    if (showPaymentModal && selectedTierForCheckout != null) {
        val tier = selectedTierForCheckout!!
        AlertDialog(
            onDismissRequest = { showPaymentModal = false },
            containerColor = ObsidianSurface,
            title = {
                Text("Konfirmasi Langganan ${tier.title}", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column {
                    Text(
                        text = "Investasi: $${tier.priceUsd} / bulan untuk ${tier.maxTerminals} akun terminal MT5.",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Status: Payment Gateway Webhook sedang dalam mode demonstrasi/staging. Konfirmasi upgrade instan dapat disimulasikan untuk pengujian.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        currentUser?.let { viewModel.setUserTier(it.id, tier) }
                        showPaymentModal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Konfirmasi & Upgrade", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPaymentModal = false }) {
                    Text("Tutup", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SubscriptionCard(
    tier: SubscriptionTier,
    isCurrent: Boolean,
    isPopular: Boolean = false,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isPopular) GoldPrimary else if (isCurrent) EmeraldProfit else ObsidianBorder,
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(tier.title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Hingga ${tier.maxTerminals} Akun MT5", color = TextMuted, fontSize = 11.sp)
                }

                if (isPopular) {
                    Text(
                        text = "PALING POPULER",
                        color = Color(0xFF1E1500),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GoldPrimary)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                } else if (isCurrent) {
                    Text(
                        text = "PAKET AKTIF",
                        color = EmeraldProfit,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EmeraldBg)
                            .border(1.dp, EmeraldProfit.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$${tier.priceUsd}", color = GoldPrimary, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text(" / bulan", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = ObsidianBorder)
            Spacer(modifier = Modifier.height(10.dp))

            tier.features.forEach { feature ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldProfit, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feature, color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSelect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrent) ObsidianSurfaceElevated else GoldPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("select_tier_${tier.name}")
            ) {
                Text(
                    text = if (isCurrent) "Paket Aktif Saat Ini" else "Pilih Paket ${tier.title}",
                    color = if (isCurrent) TextSecondary else Color(0xFF1E1500),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
