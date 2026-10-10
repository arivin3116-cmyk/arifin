package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Terminal
import com.example.ui.components.LuxurySectionHeader
import com.example.ui.components.StatusPill
import com.example.ui.theme.*
import com.example.ui.viewmodel.SuperBrainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TerminalsScreen(
    viewModel: SuperBrainViewModel
) {
    val terminals by viewModel.terminals.collectAsState()
    val selectedTerminalId by viewModel.selectedTerminalId.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        // Banner & Add button
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
                                text = "MANAJEMEN TERMINAL MT5",
                                color = GoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Kelola koneksi VPS MetaTrader 5 multi-akun Anda",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_terminal_dialog_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF1E1500), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Keamanan Terjamin: Platform ini TIDAK memerlukan investor password ataupun master password MT5 Anda. Komunikasi terjadi melalui Token Terminal terenkripsi unik.",
                        color = EmeraldProfit,
                        fontSize = 11.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // List of Terminals
        item {
            LuxurySectionHeader(
                title = "Terminal Terhubung (${terminals.size})",
                subtitle = "Klik terminal untuk memilih sebagai target monitoring aktif"
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(terminals) { term ->
            val isSelected = term.id == selectedTerminalId
            TerminalCardItem(
                terminal = term,
                isSelected = isSelected,
                onSelect = { viewModel.selectTerminal(term.id) },
                onCopyToken = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Terminal Token", term.token)
                    clipboard.setPrimaryClip(clip)
                    viewModel.showMessage("Token terminal ${term.token} disalin ke clipboard!")
                },
                onDelete = {
                    viewModel.deleteTerminal(term.id)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Setup Guide on VPS
        item {
            LuxurySectionHeader(
                title = "Panduan Pemasangan VPS Windows & MT5",
                subtitle = "Langkah menghubungkan EA dengan antarmuka web SuperBrain"
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    StepItem(
                        number = "1",
                        title = "Aktifkan WebRequest di MT5",
                        desc = "Di menu MT5 VPS, klik Tools -> Options -> Expert Advisors -> Centang 'Allow WebRequest for listed URL' dan tambahkan URL API SuperBrain: https://api.superbrain-ea.com"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StepItem(
                        number = "2",
                        title = "Pasang File EA di Folder Experts",
                        desc = "Salin file 'WorldClass_SuperBrain_Scalper_Cloud.mq5' ke folder Data MT5: MQL5 -> Experts -> lalu Compile di MetaEditor."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StepItem(
                        number = "3",
                        title = "Masukkan Token Terminal",
                        desc = "Attach EA ke chart XAUUSD M5, buka tab Inputs, dan tempel Token Terminal Anda di parameter 'InpTerminalToken'."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StepItem(
                        number = "4",
                        title = "Auto-Trading Aktif",
                        desc = "Pastikan tombol 'Algo Trading' pada toolbar MT5 berwarna hijau. Dalam 5 detik, status terminal di website akan berubah menjadi ONLINE."
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Add Terminal Dialog
    if (showAddDialog) {
        AddTerminalDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, broker, account, server, symbol ->
                viewModel.addNewTerminal(name, broker, account, server, symbol)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TerminalCardItem(
    terminal: Terminal,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onCopyToken: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .border(
                1.dp,
                if (isSelected) GoldPrimary else ObsidianBorder,
                RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ObsidianSurfaceElevated else ObsidianSurface
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(GoldPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = terminal.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                StatusPill(connectionStatus = terminal.connectionStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${terminal.broker} • Akun #${terminal.accountNumber} • Server: ${terminal.serverName}",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Text(
                text = "Simbol: ${terminal.symbol} (${terminal.timeframe}) • EA: ${terminal.eaName} v${terminal.eaVersion}",
                color = TextMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = ObsidianBorder)
            Spacer(modifier = Modifier.height(8.dp))

            // Token Bar with Copy Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ObsidianBackground)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TOKEN TERMINAL RAHASIA (MQL5 INPUT)", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(terminal.token, color = GoldLight, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                }

                IconButton(
                    onClick = onCopyToken,
                    modifier = Modifier.size(28.dp).testTag("copy_token_${terminal.id}")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Salin Token", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val timeFmt = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault()).format(Date(terminal.lastHeartbeat))
                Text(
                    text = "Heartbeat: $timeFmt • Config v${terminal.appliedConfigVersion}",
                    color = TextMuted,
                    fontSize = 10.sp
                )

                TextButton(
                    onClick = onDelete,
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Hapus", color = RubyLoss, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun StepItem(number: String, title: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(GoldPrimary.copy(alpha = 0.2f))
                .border(1.dp, GoldPrimary, androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AddTerminalDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, broker: String, account: String, server: String, symbol: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var broker by remember { mutableStateOf("Exness (Raw Spread)") }
    var account by remember { mutableStateOf("") }
    var server by remember { mutableStateOf("Exness-Real12") }
    var symbol by remember { mutableStateOf("XAUUSD") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ObsidianSurface,
        title = {
            Text("Tambah Terminal MT5 Baru", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column {
                Text("Daftarkan terminal VPS MT5 Anda untuk mendapatkan token otentikasi unik.", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Terminal (contoh: VPS SG Scalper 03)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth().testTag("add_term_name_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = broker,
                    onValueChange = { broker = it },
                    label = { Text("Nama Broker (contoh: Exness, IC Markets)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = account,
                    onValueChange = { account = it },
                    label = { Text("Nomor Akun MT5 (contoh: 8891024)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = server,
                    onValueChange = { server = it },
                    label = { Text("Nama Server Broker (contoh: Exness-Real12)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = symbol,
                    onValueChange = { symbol = it },
                    label = { Text("Simbol Trading (default: XAUUSD)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary, unfocusedBorderColor = ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(errorMsg ?: "", color = RubyLoss, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || account.isBlank()) {
                        errorMsg = "Nama terminal dan nomor akun tidak boleh kosong!"
                        return@Button
                    }
                    onAdd(name, broker, account, server, symbol)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                modifier = Modifier.testTag("submit_add_terminal_button")
            ) {
                Text("Daftarkan & Dapatkan Token", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        }
    )
}
