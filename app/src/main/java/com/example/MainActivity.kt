package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SuperBrainBottomNavigation
import com.example.ui.components.SuperBrainDrawerContent
import com.example.ui.components.SuperBrainTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBackground
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.SuperBrainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SuperBrainApp()
            }
        }
    }
}

@Composable
fun SuperBrainApp(
    viewModel: SuperBrainViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val terminals by viewModel.terminals.collectAsState()
    val selectedTerminal by viewModel.selectedTerminal.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.dismissMessage()
        }
    }

    // Handle back button smoothly
    BackHandler(enabled = currentScreen != ScreenDestination.DASHBOARD && currentScreen != ScreenDestination.LANDING) {
        if (currentUser != null) {
            viewModel.navigateTo(ScreenDestination.DASHBOARD)
        } else {
            viewModel.navigateTo(ScreenDestination.LANDING)
        }
    }

    // Public / Authentication Screens without Top/Bottom Bar scaffolding
    val isAuthScreen = currentScreen in listOf(
        ScreenDestination.LANDING,
        ScreenDestination.LOGIN,
        ScreenDestination.REGISTER,
        ScreenDestination.FORGOT_PASSWORD
    )

    if (isAuthScreen && currentUser == null) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = ObsidianBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    ScreenDestination.LANDING -> LandingScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    ScreenDestination.LOGIN -> LoginScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    ScreenDestination.REGISTER -> RegisterScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    ScreenDestination.FORGOT_PASSWORD -> ForgotPasswordScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                    else -> LandingScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            }
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                SuperBrainDrawerContent(
                    currentScreen = currentScreen,
                    currentUser = currentUser,
                    onNavigate = { dest ->
                        viewModel.navigateTo(dest)
                        coroutineScope.launch { drawerState.close() }
                    },
                    onCloseDrawer = { coroutineScope.launch { drawerState.close() } },
                    onLogout = { viewModel.logout() }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    SuperBrainTopBar(
                        currentScreen = currentScreen,
                        selectedTerminal = selectedTerminal,
                        allTerminals = terminals,
                        currentUser = currentUser,
                        onSelectTerminal = { viewModel.selectTerminal(it) },
                        onNavigate = { viewModel.navigateTo(it) },
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onLogout = { viewModel.logout() }
                    )
                },
                bottomBar = {
                    SuperBrainBottomNavigation(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                containerColor = ObsidianBackground
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        ScreenDestination.LANDING -> LandingScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        ScreenDestination.DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        ScreenDestination.LOT_SETTINGS -> LotSettingsScreen(
                            viewModel = viewModel
                        )
                        ScreenDestination.STRATEGY_SETTINGS -> StrategySettingsScreen(
                            viewModel = viewModel
                        )
                        ScreenDestination.CONTROL_CENTER -> ControlCenterScreen(
                            viewModel = viewModel
                        )
                        ScreenDestination.TERMINALS -> TerminalsScreen(
                            viewModel = viewModel
                        )
                        ScreenDestination.CONFIG_HISTORY -> ConfigHistoryScreen(
                            viewModel = viewModel
                        )
                        ScreenDestination.SUBSCRIPTION -> SubscriptionScreen(
                            viewModel = viewModel
                        )
                        ScreenDestination.ADMIN_PANEL -> AdminPanelScreen(
                            viewModel = viewModel
                        )
                        ScreenDestination.MQL5_INTEGRATION -> Mql5IntegrationScreen(
                            viewModel = viewModel
                        )
                        ScreenDestination.PROFILE -> ProfileScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                        else -> DashboardScreen(
                            viewModel = viewModel,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                }
            }
        }
    }
}
