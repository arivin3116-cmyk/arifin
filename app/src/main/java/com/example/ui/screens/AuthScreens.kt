package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SubscriptionTier
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SuperBrainViewModel

@Composable
fun LandingScreen(
    viewModel: SuperBrainViewModel,
    onNavigate: (ScreenDestination) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ObsidianSurfaceElevated)
                    .border(2.dp, GoldPrimary, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_superbrain_logo),
                    contentDescription = "SuperBrain Logo",
                    modifier = Modifier.size(72.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SUPERBRAIN EA",
                color = GoldPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = "CONTROL CENTER",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kendali EA Anda, dari Mana Saja.",
                color = GoldLight,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Platform SaaS institusional untuk monitoring dan pengelolaan remote Expert Advisor MetaTrader 5 (WorldClass SuperBrain Scalper v6.40). Ubah lot, filter EMA/ATR, jeda trading, dan emergency cut loss secara instan dari perangkat Anda.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onNavigate(ScreenDestination.REGISTER) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("landing_register_cta")
                ) {
                    Text("Daftar Sekarang", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedButton(
                    onClick = { onNavigate(ScreenDestination.LOGIN) },
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("landing_login_cta")
                ) {
                    Text("Masuk", color = GoldLight, fontWeight = FontWeight.Bold)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }

        // Core Pillars Grid
        item {
            LuxurySectionHeader(
                title = "Keunggulan Arsitektur SuperBrain",
                subtitle = "Dirancang untuk keandalan eksekusi VPS 24/7 tanpa risiko terputus"
            )
            Spacer(modifier = Modifier.height(12.dp))

            LandingFeatureCard(
                icon = Icons.Default.Tune,
                title = "Pengaturan Lot Dinamis",
                desc = "Ubah Base Lot, step multiplier, dan jarak grid tanpa perlu merestart EA atau mengganggu posisi yang sedang floating."
            )
            Spacer(modifier = Modifier.height(8.dp))
            LandingFeatureCard(
                icon = Icons.Default.Security,
                title = "Emergency Kill-Switch",
                desc = "Tombol darurat Close All dan jeda entry baru dengan konfirmasi berlapis untuk proteksi modal di volatilitas ekstrem."
            )
            Spacer(modifier = Modifier.height(8.dp))
            LandingFeatureCard(
                icon = Icons.Default.Dns,
                title = "Zero-Password Architecture",
                desc = "Tidak memerlukan password MT5 Anda. Terhubung aman via Terminal Token acak yang dapat dicabut kapan saja."
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // How it works
        item {
            LuxurySectionHeader(
                title = "Cara Kerja Integrasi",
                subtitle = "Alur sinkronisasi website ke MetaTrader 5 di VPS"
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("1. Website SaaS menyimpan konfigurasi baru dengan configVersion bertambah.", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("2. EA di VPS melakukan polling HTTP WebRequest berkala (1-5 detik).", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("3. EA memperbarui variabel runtime aktif & mengirim ACK konfirmasi ke server.", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("4. Status antarmuka berubah menjadi 'Berhasil Diterapkan'.", color = EmeraldProfit, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }

        item {
            Text(
                text = "© 2026 SUPERBRAIN EA CONTROL CENTER • All Rights Reserved",
                color = TextMuted,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun LandingFeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianSurfaceElevated)
                    .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(desc, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: SuperBrainViewModel,
    onNavigate: (ScreenDestination) -> Unit
) {
    var email by remember { mutableStateOf("muhammadarifin31101996@gmail.com") }
    var password by remember { mutableStateOf("superbrain2026") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ObsidianBorderGold, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianSurfaceElevated)
                        .border(1.dp, GoldPrimary, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_superbrain_logo),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("MASUK KE KENDALI EA", color = GoldPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("SUPERBRAIN EA CONTROL CENTER", color = TextMuted, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Akun") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth().testTag("login_email_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Kata Sandi") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth().testTag("login_password_input")
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMsg ?: "", color = RubyLoss, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val success = viewModel.login(email, password)
                        if (!success) {
                            errorMsg = "Email atau kata sandi tidak valid (minimal 6 karakter)!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_login_button")
                ) {
                    Text("Masuk ke Dashboard", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { onNavigate(ScreenDestination.FORGOT_PASSWORD) }) {
                        Text("Lupa Kata Sandi?", color = TextMuted, fontSize = 11.sp)
                    }
                    TextButton(onClick = { onNavigate(ScreenDestination.REGISTER) }) {
                        Text("Daftar Akun Baru", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    viewModel: SuperBrainViewModel,
    onNavigate: (ScreenDestination) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ObsidianBorderGold, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("BUAT AKUN MEMBER", color = GoldPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text("SUPERBRAIN CONTROL CENTER", color = TextMuted, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Alamat Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Kata Sandi (min 6 karakter)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth().testTag("reg_pass_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Konfirmasi Kata Sandi") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMsg ?: "", color = RubyLoss, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank()) {
                            errorMsg = "Semua kolom wajib diisi!"
                            return@Button
                        }
                        if (password != confirmPassword) {
                            errorMsg = "Konfirmasi kata sandi tidak cocok!"
                            return@Button
                        }
                        val success = viewModel.register(name, email, password)
                        if (!success) {
                            errorMsg = "Gagal membuat akun. Pastikan data valid."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("submit_register_button")
                ) {
                    Text("Daftar Akun Baru", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = { onNavigate(ScreenDestination.LOGIN) }) {
                    Text("Sudah punya akun? Masuk di sini", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    viewModel: SuperBrainViewModel,
    onNavigate: (ScreenDestination) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var sentMessage by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("RESET KATA SANDI", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Masukkan email terdaftar Anda untuk menerima tautan verifikasi reset kata sandi.", color = TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Akun") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                if (sentMessage) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Instruksi reset kata sandi telah dikirim ke email Anda!", color = EmeraldProfit, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            sentMessage = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kirim Tautan Reset", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { onNavigate(ScreenDestination.LOGIN) }) {
                    Text("Kembali ke Halaman Masuk", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: SuperBrainViewModel,
    onNavigate: (ScreenDestination) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser ?: return

    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorderGold, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.15f))
                            .border(2.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user.name.take(1).uppercase(), color = GoldPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(user.name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(user.email, color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Role: ${user.role.label} • Paket: ${user.tier.title}", color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            LuxurySectionHeader(title = "Ubah Informasi Profil")
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Lengkap") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Akun") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.updateUserProfile(name, email) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("save_profile_button")
            ) {
                Text("Simpan Perubahan Profil", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        item {
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = RubyLoss.copy(alpha = 0.2f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, RubyLoss.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Keluar dari Sesi (Logout)", color = RubyLoss, fontWeight = FontWeight.Bold)
            }
        }
    }
}
