package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.SuperBrainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenDestination {
    LANDING,
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    DASHBOARD,
    LOT_SETTINGS,
    STRATEGY_SETTINGS,
    TERMINALS,
    CONTROL_CENTER,
    CONFIG_HISTORY,
    SUBSCRIPTION,
    ADMIN_PANEL,
    MQL5_INTEGRATION,
    PROFILE
}

class SuperBrainViewModel(
    val repository: SuperBrainRepository = SuperBrainRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow(ScreenDestination.DASHBOARD)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    val currentUser = repository.currentUser
    val allUsers = repository.allUsers
    val terminals = repository.terminals
    val selectedTerminalId = repository.selectedTerminalId
    val commands = repository.commands
    val auditLogs = repository.auditLogs
    val equityHistory = repository.equityHistory

    // Selected terminal derived
    val selectedTerminal: StateFlow<Terminal?> = combine(terminals, selectedTerminalId) { list, id ->
        list.firstOrNull { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Current Config derived
    val activeConfig: StateFlow<EAConfig> = combine(repository.configs, selectedTerminalId) { map, id ->
        map[id] ?: EAConfig(id = "default-$id", terminalId = id)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EAConfig(id = "default", terminalId = "none"))

    // Transient UI Notification/Banner
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Emergency Close All Confirmation Dialog State
    private val _showCloseAllDialog = MutableStateFlow(false)
    val showCloseAllDialog: StateFlow<Boolean> = _showCloseAllDialog.asStateFlow()

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    fun selectTerminal(id: String) {
        repository.selectTerminal(id)
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun dismissMessage() {
        _userMessage.value = null
    }

    fun openCloseAllDialog() {
        _showCloseAllDialog.value = true
    }

    fun closeCloseAllDialog() {
        _showCloseAllDialog.value = false
    }

    fun executeEmergencyCloseAll() {
        _showCloseAllDialog.value = false
        repository.dispatchCommand(CommandType.EMERGENCY_CLOSE_ALL)
        showMessage("Instruksi CLOSE ALL dikirim ke antrean cloud terminal!")
    }

    fun executeCommand(type: CommandType) {
        repository.dispatchCommand(type)
        showMessage("Perintah '${type.label}' diproses...")
    }

    fun saveLotSettings(
        baseLot: Double,
        lotStepCount: Int,
        lotStepAdd: Double,
        maxPositions: Int,
        minGridDistancePoints: Int,
        recoveryLotMult: Double,
        minTPPoints: Int,
        maxSpreadPoints: Double,
        maxDrawdownPct: Double,
        dailyTargetProfit: Double,
        slippageDeviation: Long
    ) {
        val current = activeConfig.value
        val updated = current.copy(
            baseLot = baseLot,
            lotStepCount = lotStepCount,
            lotStepAdd = lotStepAdd,
            maxPositions = maxPositions,
            minGridDistancePoints = minGridDistancePoints,
            recoveryLotMult = recoveryLotMult,
            minTPPoints = minTPPoints,
            maxSpreadPoints = maxSpreadPoints,
            maxDrawdownPct = maxDrawdownPct,
            dailyTargetProfit = dailyTargetProfit,
            slippageDeviation = slippageDeviation
        )
        repository.saveConfig(updated)
        showMessage("Pengaturan Lot berhasil disimpan! Versi config dinaikkan ke v${current.version + 1}")
    }

    fun saveStrategySettings(
        useEMAFilter: Boolean,
        emaPeriodM5: Int,
        useEMAM15: Boolean,
        emaPeriodM15: Int,
        useEMAH1: Boolean,
        emaPeriodH1: Int,
        atrPeriod: Int,
        atrMultStep1: Double,
        atrMultStep2: Double,
        atrMultStep3: Double,
        atrMultInc: Double,
        useBreakoutRecovery: Boolean,
        srPeriod: Int,
        useTimeFilter: Boolean,
        startHour: Int,
        endHour: Int
    ) {
        val current = activeConfig.value
        val updated = current.copy(
            useEMAFilter = useEMAFilter,
            emaPeriodM5 = emaPeriodM5,
            useEMAM15 = useEMAM15,
            emaPeriodM15 = emaPeriodM15,
            useEMAH1 = useEMAH1,
            emaPeriodH1 = emaPeriodH1,
            atrPeriod = atrPeriod,
            atrMultStep1 = atrMultStep1,
            atrMultStep2 = atrMultStep2,
            atrMultStep3 = atrMultStep3,
            atrMultInc = atrMultInc,
            useBreakoutRecovery = useBreakoutRecovery,
            srPeriod = srPeriod,
            useTimeFilter = useTimeFilter,
            startHour = startHour,
            endHour = endHour
        )
        repository.saveConfig(updated)
        showMessage("Pengaturan Strategi berhasil disimpan! Sinkronisasi otomatis ke terminal...")
    }

    fun toggleTerminalOnline(id: String) {
        repository.toggleTerminalOnlineStatus(id)
    }

    fun addNewTerminal(name: String, broker: String, account: String, server: String, symbol: String) {
        repository.addTerminal(name, broker, account, server, symbol)
        showMessage("Terminal baru berhasil didaftarkan!")
    }

    fun deleteTerminal(id: String) {
        repository.deleteTerminal(id)
        showMessage("Terminal dihapus dari daftar monitoring.")
    }

    fun login(email: String, pass: String): Boolean {
        val success = repository.login(email, pass)
        if (success) {
            _currentScreen.value = ScreenDestination.DASHBOARD
            showMessage("Selamat datang kembali di SuperBrain Control Center!")
        }
        return success
    }

    fun register(name: String, email: String, pass: String): Boolean {
        val success = repository.register(name, email, pass)
        if (success) {
            _currentScreen.value = ScreenDestination.DASHBOARD
            showMessage("Akun berhasil dibuat! Silakan hubungkan terminal EA Anda.")
        }
        return success
    }

    fun logout() {
        repository.logout()
        _currentScreen.value = ScreenDestination.LOGIN
        showMessage("Sesi telah diakhiri dengan aman.")
    }

    fun updateUserProfile(name: String, email: String) {
        repository.updateUserProfile(name, email)
        showMessage("Profil berhasil diperbarui.")
    }

    fun toggleUserSuspension(userId: String) {
        repository.toggleUserSuspension(userId)
        showMessage("Status akses akun pengguna telah diubah.")
    }

    fun setUserTier(userId: String, tier: SubscriptionTier) {
        repository.setUserTier(userId, tier)
        showMessage("Paket langganan pengguna diperbarui ke ${tier.title}.")
    }
}
