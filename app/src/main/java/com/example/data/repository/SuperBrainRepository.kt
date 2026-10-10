package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class SuperBrainRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    // Current authenticated user
    private val _currentUser = MutableStateFlow<User?>(
        User(
            id = "usr_superbrain_admin",
            name = "Muhammad Arifin",
            email = "muhammadarifin31101996@gmail.com",
            role = UserRole.ADMIN,
            tier = SubscriptionTier.PRO,
            expiresAt = System.currentTimeMillis() + (45L * 24 * 60 * 60 * 1000)
        )
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // All Users for Admin Panel
    private val _allUsers = MutableStateFlow<List<User>>(
        listOf(
            User(
                id = "usr_superbrain_admin",
                name = "Muhammad Arifin",
                email = "muhammadarifin31101996@gmail.com",
                role = UserRole.ADMIN,
                tier = SubscriptionTier.ELITE
            ),
            User(
                id = "usr_trader_002",
                name = "Budi Santoso",
                email = "budi.goldtrader@gmail.com",
                role = UserRole.MEMBER,
                tier = SubscriptionTier.PRO
            ),
            User(
                id = "usr_trader_003",
                name = "David Wijaya",
                email = "david.scalper@yahoo.com",
                role = UserRole.MEMBER,
                tier = SubscriptionTier.STARTER
            ),
            User(
                id = "usr_trader_004",
                name = "Siti Rahmawati",
                email = "siti.rahma@gmail.com",
                role = UserRole.MEMBER,
                tier = SubscriptionTier.PRO,
                isSuspended = true
            )
        )
    )
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    // Terminals
    private val _terminals = MutableStateFlow<List<Terminal>>(
        listOf(
            Terminal(
                id = "term-vps-01",
                name = "VPS Singapore - XAUUSD Scalper 01",
                token = "SB-LIVE-9921-XAU",
                broker = "Exness (Raw Spread)",
                accountNumber = "8891024",
                serverName = "Exness-Real12",
                symbol = "XAUUSD",
                timeframe = "M5",
                balance = 10540.25,
                equity = 10722.50,
                floatingProfit = 182.25,
                todayProfit = 345.80,
                buyPositions = 2,
                sellPositions = 0,
                totalLot = 0.03,
                spread = 12.0,
                activePositions = 2,
                maxPositionsLimit = 20,
                tradingStatus = TradingStatus.ACTIVE,
                connectionStatus = ConnectionStatus.ONLINE,
                lastHeartbeat = System.currentTimeMillis() - 2000,
                appliedConfigVersion = 1,
                pendingConfigVersion = 1
            ),
            Terminal(
                id = "term-vps-02",
                name = "VPS Tokyo - Gold Night Grid 02",
                token = "SB-LIVE-4412-GOLD",
                broker = "IC Markets (True ECN)",
                accountNumber = "5512998",
                serverName = "ICMarkets-Live04",
                symbol = "XAUUSD",
                timeframe = "M5",
                balance = 5200.00,
                equity = 5185.50,
                floatingProfit = -14.50,
                todayProfit = 120.40,
                buyPositions = 1,
                sellPositions = 1,
                totalLot = 0.02,
                spread = 14.0,
                activePositions = 2,
                maxPositionsLimit = 20,
                tradingStatus = TradingStatus.ACTIVE,
                connectionStatus = ConnectionStatus.ONLINE,
                lastHeartbeat = System.currentTimeMillis() - 4000,
                appliedConfigVersion = 1,
                pendingConfigVersion = 1
            )
        )
    )
    val terminals: StateFlow<List<Terminal>> = _terminals.asStateFlow()

    private val _selectedTerminalId = MutableStateFlow("term-vps-01")
    val selectedTerminalId: StateFlow<String> = _selectedTerminalId.asStateFlow()

    // Configs keyed by terminal ID
    private val _configs = MutableStateFlow<Map<String, EAConfig>>(
        mapOf(
            "term-vps-01" to EAConfig(
                id = "cfg-term-01",
                terminalId = "term-vps-01",
                version = 1,
                appliedStatus = ConfigAppliedStatus.APPLIED
            ),
            "term-vps-02" to EAConfig(
                id = "cfg-term-02",
                terminalId = "term-vps-02",
                version = 1,
                appliedStatus = ConfigAppliedStatus.APPLIED
            )
        )
    )
    val configs: StateFlow<Map<String, EAConfig>> = _configs.asStateFlow()

    // Commands history
    private val _commands = MutableStateFlow<List<ControlCommand>>(
        listOf(
            ControlCommand(
                id = "cmd-1",
                terminalId = "term-vps-01",
                type = CommandType.FORCE_SYNC_CONFIG,
                status = CommandStatus.ACKNOWLEDGED,
                createdAt = System.currentTimeMillis() - 3600000,
                executedAt = System.currentTimeMillis() - 3598000,
                resultMessage = "Konfigurasi v1 sinkron dengan EA MT5"
            )
        )
    )
    val commands: StateFlow<List<ControlCommand>> = _commands.asStateFlow()

    // Audit Logs
    private val _auditLogs = MutableStateFlow<List<AuditLog>>(
        listOf(
            AuditLog(
                id = "log-1",
                terminalId = "term-vps-01",
                title = "Terminal Terkoneksi",
                description = "WorldClass_SuperBrain_Scalper v6.40 terdeteksi online di VPS Singapore",
                category = LogCategory.SYSTEM,
                severity = LogSeverity.SUCCESS,
                timestamp = System.currentTimeMillis() - 7200000
            ),
            AuditLog(
                id = "log-2",
                terminalId = "term-vps-01",
                title = "Konfigurasi v1 Diterapkan",
                description = "Base lot 0.01, Max positions 20, TP 10 pts telah aktif di terminal",
                category = LogCategory.CONFIG,
                severity = LogSeverity.INFO,
                timestamp = System.currentTimeMillis() - 3600000
            )
        )
    )
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    // Historical Equity points for chart
    val equityHistory: List<EquityPoint> = listOf(
        EquityPoint("08:00", 10000.0, 10000.0),
        EquityPoint("10:00", 10120.0, 10050.0),
        EquityPoint("12:00", 10250.0, 10180.0),
        EquityPoint("14:00", 10190.0, 10210.0),
        EquityPoint("16:00", 10450.0, 10320.0),
        EquityPoint("18:00", 10580.0, 10490.0),
        EquityPoint("20:00", 10722.5, 10540.25)
    )

    init {
        // Start background telemetry simulation loop
        startTelemetryLoop()
    }

    fun selectTerminal(terminalId: String) {
        _selectedTerminalId.value = terminalId
    }

    fun getSelectedTerminal(): Terminal? {
        return _terminals.value.firstOrNull { it.id == _selectedTerminalId.value }
    }

    fun getSelectedConfig(): EAConfig {
        val termId = _selectedTerminalId.value
        return _configs.value[termId] ?: EAConfig(id = UUID.randomUUID().toString(), terminalId = termId)
    }

    fun saveConfig(updatedConfig: EAConfig) {
        val terminal = getSelectedTerminal() ?: return
        val currentVer = updatedConfig.version
        val newVer = currentVer + 1
        val configWithNewVersion = updatedConfig.copy(
            version = newVer,
            updatedAt = System.currentTimeMillis(),
            appliedStatus = if (terminal.connectionStatus == ConnectionStatus.ONLINE) ConfigAppliedStatus.PENDING else ConfigAppliedStatus.OFFLINE,
            appliedMessage = if (terminal.connectionStatus == ConnectionStatus.ONLINE) "Menunggu polling heartbeat EA dari VPS..." else "Terminal offline - akan diterapkan saat online"
        )

        val updatedMap = _configs.value.toMutableMap()
        updatedMap[terminal.id] = configWithNewVersion
        _configs.value = updatedMap

        // Update terminal pending version
        _terminals.value = _terminals.value.map {
            if (it.id == terminal.id) it.copy(pendingConfigVersion = newVer) else it
        }

        // Add audit log
        addAuditLog(
            terminalId = terminal.id,
            title = "Pembaruan Parameter (v$newVer)",
            description = "Lot Awal: ${updatedConfig.baseLot}, Max Pos: ${updatedConfig.maxPositions}, TP: ${updatedConfig.minTPPoints} pts",
            category = LogCategory.CONFIG,
            severity = LogSeverity.INFO
        )

        // If online, simulate EA acknowledgement after 2 seconds
        if (terminal.connectionStatus == ConnectionStatus.ONLINE) {
            scope.launch {
                delay(2200)
                val finalMap = _configs.value.toMutableMap()
                finalMap[terminal.id] = configWithNewVersion.copy(
                    appliedStatus = ConfigAppliedStatus.APPLIED,
                    appliedMessage = "Telah diterapkan oleh EA MT5 pada runtime"
                )
                _configs.value = finalMap

                _terminals.value = _terminals.value.map {
                    if (it.id == terminal.id) it.copy(appliedConfigVersion = newVer) else it
                }

                addAuditLog(
                    terminalId = terminal.id,
                    title = "Konfigurasi v$newVer Berhasil Diterapkan",
                    description = "Terminal MT5 telah mengonfirmasi aktivasi parameter via WebRequest ACK",
                    category = LogCategory.CONFIG,
                    severity = LogSeverity.SUCCESS
                )
            }
        }
    }

    fun dispatchCommand(type: CommandType, note: String = "") {
        val terminal = getSelectedTerminal() ?: return
        val cmdId = "cmd-" + System.currentTimeMillis()
        val newCommand = ControlCommand(
            id = cmdId,
            terminalId = terminal.id,
            type = type,
            status = CommandStatus.QUEUED,
            createdAt = System.currentTimeMillis()
        )

        _commands.value = listOf(newCommand) + _commands.value

        addAuditLog(
            terminalId = terminal.id,
            title = "Perintah: ${type.label}",
            description = if (type.isHighRisk) "PERINGATAN RISIKO TINGGI: Perintah darurat dikirim ke antrean" else "Perintah dikirim ke antrean cloud",
            category = LogCategory.COMMAND,
            severity = if (type.isHighRisk) LogSeverity.DANGER else LogSeverity.INFO
        )

        // Process dispatch
        scope.launch {
            delay(800)
            _commands.value = _commands.value.map {
                if (it.id == cmdId) it.copy(status = CommandStatus.SENT) else it
            }

            delay(1500)
            val resultMsg = when (type) {
                CommandType.PAUSE_NEW_ENTRIES -> {
                    _terminals.value = _terminals.value.map {
                        if (it.id == terminal.id) it.copy(tradingStatus = TradingStatus.PAUSED) else it
                    }
                    "Entry baru berhasil dijeda di terminal"
                }
                CommandType.RESUME_TRADING -> {
                    _terminals.value = _terminals.value.map {
                        if (it.id == terminal.id) it.copy(tradingStatus = TradingStatus.ACTIVE) else it
                    }
                    "Trading aktif kembali di terminal"
                }
                CommandType.FORCE_SYNC_CONFIG -> {
                    "Semua parameter disinkronkan ulang dengan MT5"
                }
                CommandType.EMERGENCY_CLOSE_ALL -> {
                    _terminals.value = _terminals.value.map {
                        if (it.id == terminal.id) it.copy(
                            buyPositions = 0,
                            sellPositions = 0,
                            activePositions = 0,
                            totalLot = 0.0,
                            floatingProfit = 0.0
                        ) else it
                    }
                    "Seluruh 2 posisi terbuka berhasil ditutup darurat (Close All confirmed)"
                }
            }

            _commands.value = _commands.value.map {
                if (it.id == cmdId) it.copy(
                    status = CommandStatus.ACKNOWLEDGED,
                    executedAt = System.currentTimeMillis(),
                    resultMessage = resultMsg
                ) else it
            }

            addAuditLog(
                terminalId = terminal.id,
                title = "Eksekusi: ${type.label}",
                description = resultMsg,
                category = LogCategory.COMMAND,
                severity = LogSeverity.SUCCESS
            )
        }
    }

    fun toggleTerminalOnlineStatus(terminalId: String) {
        _terminals.value = _terminals.value.map {
            if (it.id == terminalId) {
                val newStatus = if (it.connectionStatus == ConnectionStatus.ONLINE) ConnectionStatus.OFFLINE else ConnectionStatus.ONLINE
                addAuditLog(
                    terminalId = terminalId,
                    title = if (newStatus == ConnectionStatus.ONLINE) "Terminal Online Kembali" else "Terminal Terputus (Offline)",
                    description = if (newStatus == ConnectionStatus.ONLINE) "Heartbeat kembali diterima dari VPS" else "Koneksi VPS terputus",
                    category = LogCategory.SYSTEM,
                    severity = if (newStatus == ConnectionStatus.ONLINE) LogSeverity.SUCCESS else LogSeverity.WARNING
                )
                it.copy(
                    connectionStatus = newStatus,
                    lastHeartbeat = System.currentTimeMillis()
                )
            } else it
        }
    }

    fun addTerminal(name: String, broker: String, accountNumber: String, serverName: String, symbol: String) {
        val newId = "term-vps-" + (System.currentTimeMillis() % 10000)
        val token = "SB-LIVE-${(1000..9999).random()}-${symbol.take(3)}"
        val newTerminal = Terminal(
            id = newId,
            name = name,
            token = token,
            broker = broker,
            accountNumber = accountNumber,
            serverName = serverName,
            symbol = symbol,
            timeframe = "M5",
            balance = 10000.0,
            equity = 10000.0,
            floatingProfit = 0.0,
            todayProfit = 0.0,
            buyPositions = 0,
            sellPositions = 0,
            totalLot = 0.0,
            spread = 15.0,
            activePositions = 0,
            connectionStatus = ConnectionStatus.ONLINE,
            lastHeartbeat = System.currentTimeMillis()
        )
        _terminals.value = _terminals.value + newTerminal
        val newConfig = EAConfig(id = "cfg-$newId", terminalId = newId)
        val currentConfigs = _configs.value.toMutableMap()
        currentConfigs[newId] = newConfig
        _configs.value = currentConfigs
        _selectedTerminalId.value = newId

        addAuditLog(
            terminalId = newId,
            title = "Terminal Baru Didaftarkan",
            description = "Terminal $name ($accountNumber) didaftarkan dengan token unik",
            category = LogCategory.SYSTEM,
            severity = LogSeverity.INFO
        )
    }

    fun deleteTerminal(terminalId: String) {
        _terminals.value = _terminals.value.filter { it.id != terminalId }
        if (_selectedTerminalId.value == terminalId && _terminals.value.isNotEmpty()) {
            _selectedTerminalId.value = _terminals.value.first().id
        }
    }

    fun addAuditLog(terminalId: String, title: String, description: String, category: LogCategory, severity: LogSeverity) {
        val newLog = AuditLog(
            id = "log-" + System.currentTimeMillis(),
            terminalId = terminalId,
            title = title,
            description = description,
            category = category,
            severity = severity,
            timestamp = System.currentTimeMillis()
        )
        _auditLogs.value = listOf(newLog) + _auditLogs.value
    }

    // Auth methods
    fun login(email: String, password: String): Boolean {
        // Password verification (simulated secure authentication)
        if (email.isNotBlank() && password.length >= 6) {
            val user = _allUsers.value.firstOrNull { it.email.equals(email, ignoreCase = true) }
                ?: User(
                    id = "usr_" + UUID.randomUUID().toString().take(8),
                    name = email.substringBefore("@").replace(".", " ").capitalizeWords(),
                    email = email,
                    role = if (email.contains("admin")) UserRole.ADMIN else UserRole.MEMBER,
                    tier = SubscriptionTier.PRO
                )
            _currentUser.value = user
            return true
        }
        return false
    }

    fun register(name: String, email: String, password: String): Boolean {
        if (name.isNotBlank() && email.isNotBlank() && password.length >= 6) {
            val newUser = User(
                id = "usr_" + UUID.randomUUID().toString().take(8),
                name = name,
                email = email,
                role = UserRole.MEMBER,
                tier = SubscriptionTier.STARTER
            )
            _allUsers.value = _allUsers.value + newUser
            _currentUser.value = newUser
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateUserProfile(name: String, email: String) {
        _currentUser.value?.let { current ->
            val updated = current.copy(name = name, email = email)
            _currentUser.value = updated
            _allUsers.value = _allUsers.value.map { if (it.id == current.id) updated else it }
        }
    }

    fun toggleUserSuspension(userId: String) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == userId) it.copy(isSuspended = !it.isSuspended) else it
        }
    }

    fun setUserTier(userId: String, tier: SubscriptionTier) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == userId) it.copy(tier = tier) else it
        }
        if (_currentUser.value?.id == userId) {
            _currentUser.value = _currentUser.value?.copy(tier = tier)
        }
    }

    private fun startTelemetryLoop() {
        scope.launch {
            while (true) {
                delay(4000)
                // Slight fluctuation for realism if terminal is online
                _terminals.value = _terminals.value.map { term ->
                    if (term.connectionStatus == ConnectionStatus.ONLINE && term.activePositions > 0) {
                        val delta = (-5..8).random() * 0.75
                        val newFloat = term.floatingProfit + delta
                        val newEquity = term.balance + newFloat
                        term.copy(
                            floatingProfit = Math.round(newFloat * 100.0) / 100.0,
                            equity = Math.round(newEquity * 100.0) / 100.0,
                            lastHeartbeat = System.currentTimeMillis()
                        )
                    } else if (term.connectionStatus == ConnectionStatus.ONLINE) {
                        term.copy(lastHeartbeat = System.currentTimeMillis())
                    } else term
                }
            }
        }
    }
}

private fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
}
