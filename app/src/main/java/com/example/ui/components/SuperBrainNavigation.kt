package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Terminal
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperBrainTopBar(
    currentScreen: ScreenDestination,
    selectedTerminal: Terminal?,
    allTerminals: List<Terminal>,
    currentUser: User?,
    onSelectTerminal: (String) -> Unit,
    onNavigate: (ScreenDestination) -> Unit,
    onOpenDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    var showTerminalDropdown by remember { mutableStateOf(false) }
    var showUserMenu by remember { mutableStateOf(false) }

    Surface(
        color = ObsidianBackground,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand Left
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenDrawer() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianSurfaceElevated)
                            .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_superbrain_logo),
                            contentDescription = "SuperBrain Logo",
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SUPERBRAIN",
                                color = GoldPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "EA",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "CONTROL CENTER • v6.40",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Action Right: Menu / User
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.testTag("open_menu_drawer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu Navigasi",
                            tint = GoldPrimary
                        )
                    }

                    Box {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary.copy(alpha = 0.15f))
                                .border(1.dp, GoldPrimary, CircleShape)
                                .clickable { showUserMenu = true }
                                .testTag("user_avatar_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.name?.take(1)?.uppercase() ?: "U",
                                color = GoldPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        DropdownMenu(
                            expanded = showUserMenu,
                            onDismissRequest = { showUserMenu = false },
                            modifier = Modifier
                                .background(ObsidianSurface)
                                .border(1.dp, ObsidianBorder, RoundedCornerShape(8.dp))
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(currentUser?.name ?: "User", color = TextPrimary, fontWeight = FontWeight.Bold)
                                        Text(currentUser?.email ?: "", color = TextSecondary, fontSize = 11.sp)
                                        Text("Role: ${currentUser?.role?.label}", color = GoldPrimary, fontSize = 10.sp)
                                    }
                                },
                                onClick = {
                                    showUserMenu = false
                                    onNavigate(ScreenDestination.PROFILE)
                                }
                            )
                            HorizontalDivider(color = ObsidianBorder)
                            DropdownMenuItem(
                                text = { Text("Pengaturan Profil", color = TextPrimary) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                                onClick = {
                                    showUserMenu = false
                                    onNavigate(ScreenDestination.PROFILE)
                                }
                            )
                            if (currentUser?.role == UserRole.ADMIN) {
                                DropdownMenuItem(
                                    text = { Text("Admin Panel", color = GoldLight) },
                                    leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldPrimary) },
                                    onClick = {
                                        showUserMenu = false
                                        onNavigate(ScreenDestination.ADMIN_PANEL)
                                    }
                                )
                            }
                            HorizontalDivider(color = ObsidianBorder)
                            DropdownMenuItem(
                                text = { Text("Keluar (Logout)", color = RubyLoss) },
                                leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null, tint = RubyLoss) },
                                onClick = {
                                    showUserMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            }

            // Terminal Selector Bar (When logged in)
            if (currentUser != null && selectedTerminal != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianSurface)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp))
                            .clickable { showTerminalDropdown = true }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("terminal_selector_bar"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = selectedTerminal.name,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${selectedTerminal.symbol} • ${selectedTerminal.broker} • Magic: 888111",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusPill(connectionStatus = selectedTerminal.connectionStatus)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Pilih Terminal",
                                tint = TextSecondary
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showTerminalDropdown,
                        onDismissRequest = { showTerminalDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(ObsidianSurface)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(8.dp))
                    ) {
                        allTerminals.forEach { term ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(term.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text("${term.symbol} | ${term.broker} (#${term.accountNumber})", color = TextMuted, fontSize = 11.sp)
                                        }
                                        StatusPill(connectionStatus = term.connectionStatus)
                                    }
                                },
                                onClick = {
                                    onSelectTerminal(term.id)
                                    showTerminalDropdown = false
                                }
                            )
                        }
                        HorizontalDivider(color = ObsidianBorder)
                        DropdownMenuItem(
                            text = { Text("+ Tambah Terminal Baru", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                            onClick = {
                                showTerminalDropdown = false
                                onNavigate(ScreenDestination.TERMINALS)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SuperBrainBottomNavigation(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit
) {
    Surface(
        color = ObsidianSurface,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .border(1.dp, ObsidianBorder.copy(alpha = 0.8f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Dashboard,
                label = "Dashboard",
                isSelected = currentScreen == ScreenDestination.DASHBOARD,
                onClick = { onNavigate(ScreenDestination.DASHBOARD) }
            )
            NavItem(
                icon = Icons.Default.Tune,
                label = "Lot EA",
                isSelected = currentScreen == ScreenDestination.LOT_SETTINGS,
                onClick = { onNavigate(ScreenDestination.LOT_SETTINGS) }
            )
            NavItem(
                icon = Icons.Default.AutoGraph,
                label = "Strategi",
                isSelected = currentScreen == ScreenDestination.STRATEGY_SETTINGS,
                onClick = { onNavigate(ScreenDestination.STRATEGY_SETTINGS) }
            )
            NavItem(
                icon = Icons.Default.Security,
                label = "Kendali",
                isSelected = currentScreen == ScreenDestination.CONTROL_CENTER,
                onClick = { onNavigate(ScreenDestination.CONTROL_CENTER) }
            )
            NavItem(
                icon = Icons.Default.Computer,
                label = "Terminal",
                isSelected = currentScreen == ScreenDestination.TERMINALS,
                onClick = { onNavigate(ScreenDestination.TERMINALS) }
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (isSelected) GoldPrimary else TextMuted
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SuperBrainDrawerContent(
    currentScreen: ScreenDestination,
    currentUser: User?,
    onNavigate: (ScreenDestination) -> Unit,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = ObsidianSurface,
        drawerContentColor = TextPrimary,
        modifier = Modifier.width(310.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(20.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianSurfaceElevated)
                        .border(1.dp, GoldPrimary, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_superbrain_logo),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("SUPERBRAIN EA", color = GoldPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                    Text("CONTROL CENTER", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Tagline: Kendali EA Anda dari Mana Saja", color = TextMuted, fontSize = 9.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = ObsidianBorder)
            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Items
            DrawerNavItem(
                icon = Icons.Default.Home,
                label = "Halaman Publik (Landing)",
                isSelected = currentScreen == ScreenDestination.LANDING,
                onClick = { onNavigate(ScreenDestination.LANDING); onCloseDrawer() }
            )
            DrawerNavItem(
                icon = Icons.Default.Dashboard,
                label = "Dashboard Monitoring",
                isSelected = currentScreen == ScreenDestination.DASHBOARD,
                onClick = { onNavigate(ScreenDestination.DASHBOARD); onCloseDrawer() }
            )
            DrawerNavItem(
                icon = Icons.Default.Tune,
                label = "Pengaturan Lot EA (Utama)",
                isSelected = currentScreen == ScreenDestination.LOT_SETTINGS,
                onClick = { onNavigate(ScreenDestination.LOT_SETTINGS); onCloseDrawer() }
            )
            DrawerNavItem(
                icon = Icons.Default.AutoGraph,
                label = "Pengaturan Strategi (EMA/ATR)",
                isSelected = currentScreen == ScreenDestination.STRATEGY_SETTINGS,
                onClick = { onNavigate(ScreenDestination.STRATEGY_SETTINGS); onCloseDrawer() }
            )
            DrawerNavItem(
                icon = Icons.Default.Computer,
                label = "Manajemen Terminal MT5",
                isSelected = currentScreen == ScreenDestination.TERMINALS,
                onClick = { onNavigate(ScreenDestination.TERMINALS); onCloseDrawer() }
            )
            DrawerNavItem(
                icon = Icons.Default.Security,
                label = "Pusat Kendali EA & Emergency",
                isSelected = currentScreen == ScreenDestination.CONTROL_CENTER,
                onClick = { onNavigate(ScreenDestination.CONTROL_CENTER); onCloseDrawer() }
            )
            DrawerNavItem(
                icon = Icons.Default.History,
                label = "Riwayat Konfigurasi & Log",
                isSelected = currentScreen == ScreenDestination.CONFIG_HISTORY,
                onClick = { onNavigate(ScreenDestination.CONFIG_HISTORY); onCloseDrawer() }
            )
            DrawerNavItem(
                icon = Icons.Default.Code,
                label = "Script MQL5 & Panduan VPS",
                isSelected = currentScreen == ScreenDestination.MQL5_INTEGRATION,
                onClick = { onNavigate(ScreenDestination.MQL5_INTEGRATION); onCloseDrawer() }
            )
            DrawerNavItem(
                icon = Icons.Default.WorkspacePremium,
                label = "Paket Langganan SaaS",
                isSelected = currentScreen == ScreenDestination.SUBSCRIPTION,
                onClick = { onNavigate(ScreenDestination.SUBSCRIPTION); onCloseDrawer() }
            )

            if (currentUser?.role == UserRole.ADMIN) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = ObsidianBorder)
                Spacer(modifier = Modifier.height(8.dp))
                DrawerNavItem(
                    icon = Icons.Default.AdminPanelSettings,
                    label = "Admin Panel",
                    isSelected = currentScreen == ScreenDestination.ADMIN_PANEL,
                    badge = "ADMIN",
                    onClick = { onNavigate(ScreenDestination.ADMIN_PANEL); onCloseDrawer() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(color = ObsidianBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // User Info Footer
            if (currentUser != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianSurfaceElevated)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(currentUser.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(currentUser.email, color = TextMuted, fontSize = 10.sp)
                        Text("Paket: ${currentUser.tier.title}", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = { onLogout(); onCloseDrawer() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = RubyLoss, modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                Button(
                    onClick = { onNavigate(ScreenDestination.LOGIN); onCloseDrawer() },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Masuk ke Akun", color = Color(0xFF1E1500), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DrawerNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    badge: String? = null,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) GoldPrimary.copy(alpha = 0.12f) else Color.Transparent
    val textColor = if (isSelected) GoldLight else TextSecondary
    val iconColor = if (isSelected) GoldPrimary else TextMuted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, color = textColor, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        }
        if (badge != null) {
            Text(
                text = badge,
                color = GoldPrimary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(GoldPrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            )
        }
    }
}
