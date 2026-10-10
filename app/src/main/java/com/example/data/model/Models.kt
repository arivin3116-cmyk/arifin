package com.example.data.model

enum class ConnectionStatus(val label: String) {
    ONLINE("Online"),
    OFFLINE("Offline"),
    UNKNOWN("Tidak Diketahui")
}

enum class TradingStatus(val label: String) {
    ACTIVE("Trading Aktif"),
    PAUSED("Entry Baru Dijeda"),
    RISK_PROTECTION("Proteksi Risiko Aktif")
}

enum class ConfigAppliedStatus(val label: String) {
    APPLIED("Berhasil Diterapkan"),
    PENDING("Menunggu Penerapan"),
    FAILED("Gagal Diterapkan"),
    OFFLINE("Terminal Offline")
}

enum class CommandType(val label: String, val isHighRisk: Boolean) {
    PAUSE_NEW_ENTRIES("Jeda Entry Baru", false),
    RESUME_TRADING("Lanjutkan Trading", false),
    FORCE_SYNC_CONFIG("Sinkronisasi Konfigurasi", false),
    EMERGENCY_CLOSE_ALL("Tutup Seluruh Posisi (Close All)", true)
}

enum class CommandStatus(val label: String) {
    QUEUED("Dalam Antrean"),
    SENT("Terkirim ke Terminal"),
    ACKNOWLEDGED("Dikonfirmasi Berhasil"),
    FAILED("Eksekusi Gagal")
}

enum class LogCategory(val label: String) {
    CONFIG("Konfigurasi"),
    COMMAND("Perintah Kendali"),
    SYSTEM("Sistem"),
    TELEMETRY("Telemetri")
}

enum class LogSeverity {
    INFO, SUCCESS, WARNING, DANGER
}

enum class UserRole(val label: String) {
    ADMIN("Administrator"),
    MEMBER("Member Trader")
}

enum class SubscriptionTier(
    val title: String,
    val priceUsd: Int,
    val maxTerminals: Int,
    val features: List<String>
) {
    STARTER(
        title = "Starter",
        priceUsd = 29,
        maxTerminals = 1,
        features = listOf(
            "1 Akun Terminal MT5",
            "Pengaturan Lot Dinamis",
            "Sinkronisasi Realtime 5 detik",
            "Kontrol Darurat Close All",
            "Dukungan Komunitas"
        )
    ),
    PRO(
        title = "Professional",
        priceUsd = 69,
        maxTerminals = 3,
        features = listOf(
            "3 Akun Terminal MT5",
            "Pengaturan Lengkap Strategi EMA/ATR",
            "Sinkronisasi Kilat 1 detik",
            "Pusat Kendali Multi-Terminal",
            "Audit Log & Riwayat Parameter Tak Terbatas",
            "Dukungan Prioritas 24/7"
        )
    ),
    ELITE(
        title = "Institutional Elite",
        priceUsd = 149,
        maxTerminals = 10,
        features = listOf(
            "10+ Akun Terminal MT5",
            "Akses API Langsung & Custom Webhook",
            "Mode Hedging Recovery Otomatis",
            "Proteksi Slippage & Spread Institusional",
            "Dedicated VPS Optimization Engineer",
            "Dukungan Personal 1-on-1 VIP"
        )
    )
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole = UserRole.MEMBER,
    val tier: SubscriptionTier = SubscriptionTier.PRO,
    val isSuspended: Boolean = false,
    val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)
)

data class Terminal(
    val id: String,
    val name: String,
    val token: String,
    val eaName: String = "WorldClass_SuperBrain_Scalper",
    val eaVersion: String = "6.40",
    val broker: String = "Exness (Raw Spread)",
    val accountNumber: String = "8891024",
    val serverName: String = "Exness-Real12",
    val symbol: String = "XAUUSD",
    val timeframe: String = "M5",
    val balance: Double = 10540.25,
    val equity: Double = 10722.50,
    val floatingProfit: Double = 182.25,
    val todayProfit: Double = 345.80,
    val buyPositions: Int = 2,
    val sellPositions: Int = 0,
    val totalLot: Double = 0.03,
    val spread: Double = 12.0,
    val activePositions: Int = 2,
    val maxPositionsLimit: Int = 20,
    val tradingStatus: TradingStatus = TradingStatus.ACTIVE,
    val connectionStatus: ConnectionStatus = ConnectionStatus.ONLINE,
    val lastHeartbeat: Long = System.currentTimeMillis(),
    val appliedConfigVersion: Int = 1,
    val pendingConfigVersion: Int = 1
)

data class EAConfig(
    val id: String,
    val terminalId: String,
    val version: Int = 1,
    val updatedAt: Long = System.currentTimeMillis(),
    val appliedStatus: ConfigAppliedStatus = ConfigAppliedStatus.APPLIED,
    val appliedMessage: String = "Telah aktif di terminal VPS",
    
    // Group 1: Scalping & Lot Settings
    val baseLot: Double = 0.01,
    val lotStepCount: Int = 1,
    val lotStepAdd: Double = 0.1,
    val minTPPoints: Int = 10,
    val magicNumber: Long = 888111L,
    
    // Group 2: Pembatas Layer
    val maxPositions: Int = 20,
    val minGridDistancePoints: Int = 30,
    
    // Group 3: Trend Filter EMA
    val useEMAFilter: Boolean = true,
    val emaPeriodM5: Int = 50,
    val useEMAM15: Boolean = true,
    val emaPeriodM15: Int = 50,
    val useEMAH1: Boolean = true,
    val emaPeriodH1: Int = 50,
    
    // Group 4: ATR Grid
    val atrPeriod: Int = 14,
    val atrMultStep1: Double = 0.2,
    val atrMultStep2: Double = 0.5,
    val atrMultStep3: Double = 1.0,
    val atrMultInc: Double = 0.5,
    
    // Group 5: Breakout Support/Resistance
    val useBreakoutRecovery: Boolean = true,
    val srPeriod: Int = 20,
    val recoveryLotMult: Double = 1.5,
    
    // Group 6: Protection & Risk Management
    val maxSpreadPoints: Double = 35.0,
    val maxDrawdownPct: Double = 20.0,
    val dailyTargetProfit: Double = 99999.0,
    val slippageDeviation: Long = 30L,
    
    // Group 7: Filter Jam Trading
    val useTimeFilter: Boolean = false,
    val startHour: Int = 8,
    val endHour: Int = 22
)

data class ControlCommand(
    val id: String,
    val terminalId: String,
    val type: CommandType,
    val status: CommandStatus = CommandStatus.QUEUED,
    val createdAt: Long = System.currentTimeMillis(),
    val executedAt: Long? = null,
    val resultMessage: String? = null
)

data class AuditLog(
    val id: String,
    val terminalId: String,
    val title: String,
    val description: String,
    val category: LogCategory,
    val severity: LogSeverity = LogSeverity.INFO,
    val timestamp: Long = System.currentTimeMillis()
)

data class EquityPoint(
    val timeLabel: String,
    val equity: Double,
    val balance: Double
)
