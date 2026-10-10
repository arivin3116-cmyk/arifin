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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionTier
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.SuperBrainViewModel

@Composable
fun AdminPanelScreen(
    viewModel: SuperBrainViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allTerminals by viewModel.terminals.collectAsState()

    // Strict Backend-Role Verification
    if (currentUser?.role != UserRole.ADMIN) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = RubyLoss, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(14.dp))
                Text("Akses Terbatas: Hanya Administrator", color = RubyLoss, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Akun Anda tidak memiliki hak otorisasi untuk mengakses Admin Panel.", color = TextMuted, fontSize = 12.sp)
            }
        }
        return
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
                    .border(1.dp, GoldPrimary, RoundedCornerShape(14.dp)),
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
                                text = "ADMIN CONTROL CONSOLE",
                                color = GoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Manajemen Member, Terminal Global, & Lisensi",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "ROOT ACCESS",
                            color = Color(0xFF1E1500),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldPrimary)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(14.dp)) }

        // Global Platform Stats
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                AdminStatBox(title = "TOTAL MEMBER", value = "${allUsers.size}", subtitle = "Pengguna Terdaftar", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                AdminStatBox(title = "TERMINAL AKTIF", value = "${allTerminals.size}", subtitle = "Terkoneksi ke VPS", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                AdminStatBox(title = "TIKET BANTUAN", value = "0", subtitle = "Semua Resolved", modifier = Modifier.weight(1f))
            }
        }

        item { Spacer(modifier = Modifier.height(18.dp)) }

        // Member Management List
        item {
            LuxurySectionHeader(
                title = "Manajemen Member & Status Akses",
                subtitle = "Kontrol penangguhan akun, role, dan alokasi batas terminal"
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(allUsers) { user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, if (user.isSuspended) RubyLoss.copy(alpha = 0.5f) else ObsidianBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(user.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = user.role.label,
                                    color = if (user.role == UserRole.ADMIN) GoldLight else CyanAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ObsidianSurfaceElevated)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(user.email, color = TextMuted, fontSize = 11.sp)
                        }

                        if (user.isSuspended) {
                            Text(
                                text = "DITANGGUHKAN",
                                color = RubyLoss,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(RubyBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        } else {
                            Text(
                                text = "AKTIF",
                                color = EmeraldProfit,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = ObsidianBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Paket: ${user.tier.title} (${user.tier.maxTerminals} Terminal)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Row {
                            TextButton(
                                onClick = {
                                    val nextTier = when (user.tier) {
                                        SubscriptionTier.STARTER -> SubscriptionTier.PRO
                                        SubscriptionTier.PRO -> SubscriptionTier.ELITE
                                        SubscriptionTier.ELITE -> SubscriptionTier.STARTER
                                    }
                                    viewModel.setUserTier(user.id, nextTier)
                                }
                            ) {
                                Text("Ganti Paket", color = GoldPrimary, fontSize = 11.sp)
                            }

                            if (user.role != UserRole.ADMIN) {
                                TextButton(
                                    onClick = { viewModel.toggleUserSuspension(user.id) }
                                ) {
                                    Text(
                                        text = if (user.isSuspended) "Buka Suspend" else "Tangguhkan",
                                        color = if (user.isSuspended) EmeraldProfit else RubyLoss,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun AdminStatBox(title: String, value: String, subtitle: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = GoldLight, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, color = TextMuted, fontSize = 9.sp)
        }
    }
}
