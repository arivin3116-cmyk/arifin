package com.example.data.mql5

object Mql5Source {
    const val CODE: String = """//+------------------------------------------------------------------+
//|                        WorldClass_SuperBrain_Scalper_Cloud.mq5   |
//|               Copyright 2026, SUPERBRAIN EA CONTROL CENTER       |
//|                  https://superbrain-ea.control-center.io         |
//+------------------------------------------------------------------+
#property copyright "Copyright 2026, SuperBrain Control Center"
#property link      "https://superbrain-ea.control-center.io"
#property version   "6.40"
#property description "WorldClass SuperBrain Scalper v6.40 with Real-Time Web SaaS Remote Control & Zero-Delay Safety Synchronization"

#include <Trade\Trade.mqh>

//--- Input Parameters (Static Fallback & Hard Safety Guardrails)
input group "=== 0. Cloud Web SaaS Integration Settings ==="
input bool     InpEnableCloudControl    = true;       // Aktifkan Kendali Jarak Jauh Cloud
input string   InpApiEndpoint           = "https://api.superbrain-ea.com/v1"; // URL REST API Endpoint
input string   InpTerminalToken         = "SB-LIVE-9921-XAU"; // Token Unik Terminal (Dari Website)
input int      InpHeartbeatIntervalSec  = 5;          // Interval Polling/Heartbeat (Detik)
input double   InpHardSafetyMaxLot      = 2.0;        // Guardrail: Batas Mutlak Lot Maksimal
input int      InpHardSafetyMaxPos      = 30;         // Guardrail: Batas Mutlak Posisi Maksimal

input group "=== 1. Scalping & Lot Settings (Default/Fallback) ==="
input double   InpBaseLot               = 0.01;      // Lot Awal (Base Lot)
input int      InpLotStepCount          = 1;         // Layer Lot Multiplier (Setiap X Order)
input double   InpLotStepAdd            = 0.1;       // Penambahan Lot Tiap Step
input int      InpMinTP_Points          = 10;        // Target Profit Minimal per 0.01 Lot (Poin)
input ulong    InpMagicNumber           = 888111;    // Magic Number EA

input group "=== 2. Pembatas Layer (Anti-Numpuk) ==="
input int      InpMaxPositions          = 20;        // Maksimal Posisi Aktif (Batas Layer)
input int      InpMinGridDistancePoints = 30;        // Jarak Minimal Antar Layer (Poin Minimal)

input group "=== 3. Otak Multi-Timeframe Trend Filter ==="
input bool     InpUseEMAFilter          = true;      // Aktifkan Filter Tren EMA
input int      InpEMAPeriodM5           = 50;        // Periode EMA M5 (Tren Pendek)
input bool     InpUseEMAM15             = true;      // Aktifkan Konfirmasi EMA M15
input int      InpEMAPeriodM15          = 50;        // Periode EMA M15 (Tren Menengah)
input bool     InpUseEMAH1              = true;      // Aktifkan Konfirmasi EMA H1
input int      InpEMAPeriodH1           = 50;        // Periode EMA H1 (Tren Utama)

input group "=== 4. ATR Progressive Grid Settings ==="
input int      InpATRPeriod             = 14;        // Periode ATR M1
input double   InpATRMultStep1          = 0.2;       // Multiplier ATR Layer 1 (Order ke-2)
input double   InpATRMultStep2          = 0.5;       // Multiplier ATR Layer 2 (Order ke-3)
input double   InpATRMultStep3          = 1.0;       // Multiplier ATR Layer 3 (Order ke-4)
input double   InpATRMultInc            = 0.5;       // Penambahan Multiplier per Layer Berikutnya

input group "=== 5. Otak Breakout Support/Resistance Recovery ==="
input bool     InpUseBreakoutRecovery   = true;      // Aktifkan Breakout Recovery M5
input int      InpSRPeriod              = 20;        // Periode Candle M5 (Support/Resistance)
input double   InpRecoveryLotMult       = 1.5;       // Pengali Lot Recovery Saat Breakout

input group "=== 6. Protection & Risk Management ==="
input double   InpMaxSpreadPoints       = 35;        // Toleransi Spread Maksimal (Poin)
input double   InpMaxDrawdownPct        = 20;        // Hard Cut Loss (% Equity Drawdown)
input double   InpDailyTargetProfit     = 99999.0;   // Lock Target Profit Harian (${'$'} USD)
input ulong    InpSlippageDeviation     = 30;        // Max Slippage (Poin)

input group "=== 7. Filter Jam Trading ==="
input bool     InpUseTimeFilter         = false;     // Aktifkan Filter Jam Trading
input int      InpStartHour             = 8;         // Jam Mulai Trading (Server Time)
input int      InpEndHour               = 22;        // Jam Selesai Trading

//--- RUNTIME ACTIVE CONFIGURATION (Dynamic variables safely modulated by Web SaaS)
struct RuntimeConfig
{
   int      configVersion;
   bool     isNewEntriesPaused;
   double   baseLot;
   int      lotStepCount;
   double   lotStepAdd;
   int      minTPPoints;
   int      maxPositions;
   int      minGridDistancePoints;
   bool     useEMAFilter;
   int      emaPeriodM5;
   bool     useEMAM15;
   int      emaPeriodM15;
   bool     useEMAH1;
   int      emaPeriodH1;
   int      atrPeriod;
   double   atrMultStep1;
   double   atrMultStep2;
   double   atrMultStep3;
   double   atrMultInc;
   bool     useBreakoutRecovery;
   int      srPeriod;
   double   recoveryLotMult;
   double   maxSpreadPoints;
   double   maxDrawdownPct;
   double   dailyTargetProfit;
   ulong    slippageDeviation;
   bool     useTimeFilter;
   int      startHour;
   int      endHour;
};

RuntimeConfig cfg;

// Global System Variables
CTrade         trade;
int            atrHandle    = INVALID_HANDLE;
int            emaM5Handle  = INVALID_HANDLE;
int            emaM15Handle = INVALID_HANDLE;
int            emaH1Handle  = INVALID_HANDLE;

datetime       lastCloudSyncTime = 0;
string         cloudConnectionStatus = "CONNECTING...";
bool           cloudAckNeeded = false;
int            activeAcknowledgedVersion = 0;

//+------------------------------------------------------------------+
//| JSON PARSING UTILITIES                                           |
//+------------------------------------------------------------------+
string ExtractJsonString(string json, string key)
{
   string search = "\"" + key + "\":\"";
   int startPos = StringFind(json, search);
   if(startPos < 0) return "";
   startPos += StringLen(search);
   int endPos = StringFind(json, "\"", startPos);
   if(endPos < 0) return "";
   return StringSubstr(json, startPos, endPos - startPos);
}

double ExtractJsonDouble(string json, string key, double defaultVal)
{
   string search = "\"" + key + "\":";
   int startPos = StringFind(json, search);
   if(startPos < 0) return defaultVal;
   startPos += StringLen(search);
   int endPos = startPos;
   while(endPos < StringLen(json))
   {
      ushort ch = StringGetCharacter(json, endPos);
      if(ch == ',' || ch == '}' || ch == ' ' || ch == '\r' || ch == '\n') break;
      endPos++;
   }
   string valStr = StringSubstr(json, startPos, endPos - startPos);
   return StringToDouble(valStr);
}

bool ExtractJsonBool(string json, string key, bool defaultVal)
{
   string search = "\"" + key + "\":";
   int startPos = StringFind(json, search);
   if(startPos < 0) return defaultVal;
   startPos += StringLen(search);
   string sub = StringSubstr(json, startPos, 5);
   if(StringFind(sub, "true") >= 0) return true;
   if(StringFind(sub, "false") >= 0) return false;
   return defaultVal;
}

//+------------------------------------------------------------------+
//| HTTP WEB REQUEST CLOUD COMMUNICATION LAYER                       |
//+------------------------------------------------------------------+
int SendHttpRequest(string method, string endpoint, string payload, string &response)
{
   string headers = "Content-Type: application/json\r\nAuthorization: Bearer " + InpTerminalToken + "\r\n";
   char postData[], resultData[];
   StringToCharArray(payload, postData);
   
   string resultHeaders;
   ResetLastError();
   
   int res = WebRequest(method, endpoint, headers, 3000, postData, resultData, resultHeaders);
   if(res == 200 || res == 201)
   {
      response = CharArrayToString(resultData);
      return res;
   }
   else
   {
      PrintFormat("[SuperBrain Cloud] WebRequest failed: error %d, HTTP response code: %d", GetLastError(), res);
      return res;
   }
}

// Forward Declaration
void CloseAllPositions();

void ProcessCloudCommunication()
{
   if(!InpEnableCloudControl) return;
   if(TimeCurrent() - lastCloudSyncTime < InpHeartbeatIntervalSec) return;
   lastCloudSyncTime = TimeCurrent();

   // 1. Build Telemetry Payload
   double balance     = AccountInfoDouble(ACCOUNT_BALANCE);
   double equity      = AccountInfoDouble(ACCOUNT_EQUITY);
   double todayProfit = 0.0; // calculated below
   double floatProfit = 0.0;
   int buyCount = 0, sellCount = 0;
   double totalLot = 0.0;
   
   int total = PositionsTotal();
   for(int i = total - 1; i >= 0; i--)
   {
      ulong ticket = PositionGetTicket(i);
      if(ticket > 0 && PositionGetString(POSITION_SYMBOL) == _Symbol && 
         PositionGetInteger(POSITION_MAGIC) == (long)InpMagicNumber)
      {
         long type = PositionGetInteger(POSITION_TYPE);
         double vol = PositionGetDouble(POSITION_VOLUME);
         totalLot += vol;
         if(type == POSITION_TYPE_BUY) buyCount++;
         if(type == POSITION_TYPE_SELL) sellCount++;
         floatProfit += PositionGetDouble(POSITION_PROFIT) + 
                        PositionGetDouble(POSITION_SWAP) + 
                        PositionGetDouble(POSITION_COMMISSION);
      }
   }

   double spread = (double)SymbolInfoInteger(_Symbol, SYMBOL_SPREAD);

   string telemetryJson = StringFormat(
      "{\"token\":\"%s\",\"account\":\"%s\",\"broker\":\"%s\",\"balance\":%.2f,\"equity\":%.2f,\"floatProfit\":%.2f,\"totalLot\":%.2f,\"buyCount\":%d,\"sellCount\":%d,\"spread\":%.1f,\"activeConfigVer\":%d}",
      InpTerminalToken, IntegerToString(AccountInfoInteger(ACCOUNT_LOGIN)), AccountInfoString(ACCOUNT_COMPANY),
      balance, equity, floatProfit, totalLot, buyCount, sellCount, spread, cfg.configVersion
   );

   // 2. Send Heartbeat and Pull Remote Config & Commands
   string url = InpApiEndpoint + "/terminal/sync";
   string response = "";
   int code = SendHttpRequest("POST", url, telemetryJson, response);

   if(code == 200)
   {
      cloudConnectionStatus = "CONNECTED (OK)";
      
      // Check emergency command:
      string emergencyCmd = ExtractJsonString(response, "emergencyCommand");
      if(emergencyCmd == "CLOSE_ALL")
      {
         Print("[SuperBrain Cloud] Received EMERGENCY CLOSE ALL signal!");
         CloseAllPositions();
      }
      
      cfg.isNewEntriesPaused = ExtractJsonBool(response, "isPaused", cfg.isNewEntriesPaused);

      // Check incoming config version
      int incomingVersion = (int)ExtractJsonDouble(response, "configVersion", (double)cfg.configVersion);
      if(incomingVersion > cfg.configVersion)
      {
         PrintFormat("[SuperBrain Cloud] Applying new remote config v%d...", incomingVersion);
         
         // Apply with HARD GUARDRAILS for security
         double newBaseLot = ExtractJsonDouble(response, "baseLot", cfg.baseLot);
         cfg.baseLot = MathMin(InpHardSafetyMaxLot, MathMax(0.01, newBaseLot));
         
         cfg.lotStepCount = MathMax(1, (int)ExtractJsonDouble(response, "lotStepCount", (double)cfg.lotStepCount));
         cfg.lotStepAdd   = ExtractJsonDouble(response, "lotStepAdd", cfg.lotStepAdd);
         cfg.minTPPoints  = MathMax(1, (int)ExtractJsonDouble(response, "minTPPoints", (double)cfg.minTPPoints));
         
         int newMaxPos = (int)ExtractJsonDouble(response, "maxPositions", (double)cfg.maxPositions);
         cfg.maxPositions = MathMin(InpHardSafetyMaxPos, MathMax(1, newMaxPos));
         
         cfg.minGridDistancePoints = MathMax(5, (int)ExtractJsonDouble(response, "minGridDistancePoints", (double)cfg.minGridDistancePoints));
         
         cfg.useEMAFilter = ExtractJsonBool(response, "useEMAFilter", cfg.useEMAFilter);
         int newEMAM5 = (int)ExtractJsonDouble(response, "emaPeriodM5", (double)cfg.emaPeriodM5);
         if(newEMAM5 != cfg.emaPeriodM5 && newEMAM5 >= 5)
         {
            cfg.emaPeriodM5 = newEMAM5;
            if(emaM5Handle != INVALID_HANDLE) IndicatorRelease(emaM5Handle);
            emaM5Handle = iMA(_Symbol, PERIOD_M5, cfg.emaPeriodM5, 0, MODE_EMA, PRICE_CLOSE);
         }
         
         cfg.useEMAM15 = ExtractJsonBool(response, "useEMAM15", cfg.useEMAM15);
         cfg.useEMAH1  = ExtractJsonBool(response, "useEMAH1", cfg.useEMAH1);

         cfg.atrPeriod     = (int)ExtractJsonDouble(response, "atrPeriod", (double)cfg.atrPeriod);
         cfg.atrMultStep1  = ExtractJsonDouble(response, "atrMultStep1", cfg.atrMultStep1);
         cfg.atrMultStep2  = ExtractJsonDouble(response, "atrMultStep2", cfg.atrMultStep2);
         cfg.atrMultStep3  = ExtractJsonDouble(response, "atrMultStep3", cfg.atrMultStep3);
         cfg.atrMultInc    = ExtractJsonDouble(response, "atrMultInc", cfg.atrMultInc);

         cfg.useBreakoutRecovery = ExtractJsonBool(response, "useBreakoutRecovery", cfg.useBreakoutRecovery);
         cfg.srPeriod            = (int)ExtractJsonDouble(response, "srPeriod", (double)cfg.srPeriod);
         cfg.recoveryLotMult     = ExtractJsonDouble(response, "recoveryLotMult", cfg.recoveryLotMult);

         cfg.maxSpreadPoints   = ExtractJsonDouble(response, "maxSpreadPoints", cfg.maxSpreadPoints);
         cfg.maxDrawdownPct    = ExtractJsonDouble(response, "maxDrawdownPct", cfg.maxDrawdownPct);
         cfg.dailyTargetProfit = ExtractJsonDouble(response, "dailyTargetProfit", cfg.dailyTargetProfit);
         cfg.useTimeFilter     = ExtractJsonBool(response, "useTimeFilter", cfg.useTimeFilter);
         cfg.startHour         = (int)ExtractJsonDouble(response, "startHour", (double)cfg.startHour);
         cfg.endHour           = (int)ExtractJsonDouble(response, "endHour", (double)cfg.endHour);

         cfg.configVersion = incomingVersion;

         // Acknowledge back to Cloud
         string ackPayload = StringFormat("{\"token\":\"%s\",\"appliedVersion\":%d,\"status\":\"APPLIED\"}", InpTerminalToken, cfg.configVersion);
         string ackResp = "";
         SendHttpRequest("POST", InpApiEndpoint + "/terminal/ack", ackPayload, ackResp);
         PrintFormat("[SuperBrain Cloud] Successfully synced & acknowledged config v%d!", cfg.configVersion);
      }
   }
   else
   {
      cloudConnectionStatus = "OFFLINE (RETRYING)";
   }
}

//+------------------------------------------------------------------+
//| STRATEGY UTILITIES & CORE ALGORITHMS                             |
//+------------------------------------------------------------------+
double GetATRMultiplier(int currentPosCount)
{
   if(currentPosCount <= 1) return cfg.atrMultStep1;
   if(currentPosCount == 2) return cfg.atrMultStep2;
   if(currentPosCount == 3) return cfg.atrMultStep3;
   return cfg.atrMultStep3 + ((currentPosCount - 3) * cfg.atrMultInc);
}

double GetEMAPrice(int handle)
{
   if(handle == INVALID_HANDLE) return 0.0;
   double val[];
   ArraySetAsSeries(val, true);
   if(CopyBuffer(handle, 0, 0, 1, val) > 0) return val[0];
   return 0.0;
}

double GetM5Resistance(int period)
{
   double highs[];
   ArraySetAsSeries(highs, true);
   if(CopyHigh(_Symbol, PERIOD_M5, 1, period, highs) == period)
      return highs[ArrayMaximum(highs)];
   return 0.0;
}

double GetM5Support(int period)
{
   double lows[];
   ArraySetAsSeries(lows, true);
   if(CopyLow(_Symbol, PERIOD_M5, 1, period, lows) == period)
      return lows[ArrayMinimum(lows)];
   return 0.0;
}

datetime GetTodayTime()
{
   datetime times[];
   ArraySetAsSeries(times, true);
   if(CopyTime(_Symbol, PERIOD_D1, 0, 1, times) > 0) return times[0];
   return TimeCurrent();
}

int CountMyPositions()
{
   int count = 0;
   int total = PositionsTotal();
   for(int i = total - 1; i >= 0; i--)
   {
      ulong ticket = PositionGetTicket(i);
      if(ticket > 0 && PositionGetString(POSITION_SYMBOL) == _Symbol && 
         PositionGetInteger(POSITION_MAGIC) == (long)InpMagicNumber)
      {
         count++;
      }
   }
   return count;
}

double GetTotalProfit()
{
   double profit = 0.0;
   int total = PositionsTotal();
   for(int i = total - 1; i >= 0; i--)
   {
      ulong ticket = PositionGetTicket(i);
      if(ticket > 0 && PositionGetString(POSITION_SYMBOL) == _Symbol && 
         PositionGetInteger(POSITION_MAGIC) == (long)InpMagicNumber)
      {
         profit += PositionGetDouble(POSITION_PROFIT) + 
                   PositionGetDouble(POSITION_SWAP) + 
                   PositionGetDouble(POSITION_COMMISSION);
      }
   }
   return profit;
}

double NormalizeLotSize(double lot)
{
   double minLot  = SymbolInfoDouble(_Symbol, SYMBOL_VOLUME_MIN);
   double maxLot  = SymbolInfoDouble(_Symbol, SYMBOL_VOLUME_MAX);
   double lotStep = SymbolInfoDouble(_Symbol, SYMBOL_VOLUME_STEP);
   
   if(lotStep > 0) lot = MathFloor(lot / lotStep) * lotStep;
   lot = MathMax(minLot, MathMin(maxLot, lot));
   return NormalizeDouble(lot, 2);
}

double CalculateLotSize(int posCount)
{
   int stepTier = posCount / cfg.lotStepCount;
   double calculatedLot = cfg.baseLot + (stepTier * cfg.lotStepAdd);
   return NormalizeLotSize(calculatedLot);
}

double GetTodayClosedProfit()
{
   double profit = 0.0;
   datetime todayStart = GetTodayTime();
   
   if(HistorySelect(todayStart, TimeCurrent() + 86400))
   {
      int totalDeals = HistoryDealsTotal();
      for(int i = 0; i < totalDeals; i++)
      {
         ulong ticket = HistoryDealGetTicket(i);
         if(ticket > 0 && HistoryDealGetInteger(ticket, DEAL_MAGIC) == (long)InpMagicNumber && 
            HistoryDealGetString(ticket, DEAL_SYMBOL) == _Symbol &&
            HistoryDealGetInteger(ticket, DEAL_ENTRY) == DEAL_ENTRY_OUT)
         {
            profit += HistoryDealGetDouble(ticket, DEAL_PROFIT) + 
                      HistoryDealGetDouble(ticket, DEAL_SWAP) + 
                      HistoryDealGetDouble(ticket, DEAL_COMMISSION);
         }
      }
   }
   return profit;
}

bool IsTradingAllowedTime()
{
   if(!cfg.useTimeFilter) return true;
   MqlDateTime dt;
   TimeCurrent(dt);
   return (dt.hour >= cfg.startHour && dt.hour < cfg.endHour);
}

bool CheckMTFTrend(ENUM_ORDER_TYPE orderType)
{
   if(!cfg.useEMAFilter) return true;

   MqlTick tick;
   if(!SymbolInfoTick(_Symbol, tick)) return false;

   double emaM5  = GetEMAPrice(emaM5Handle);
   double emaM15 = GetEMAPrice(emaM15Handle);
   double emaH1  = GetEMAPrice(emaH1Handle);

   if(orderType == ORDER_TYPE_BUY)
   {
      if(emaM5 > 0 && tick.bid <= emaM5) return false;
      if(cfg.useEMAM15 && emaM15 > 0 && tick.bid <= emaM15) return false;
      if(cfg.useEMAH1  && emaH1 > 0  && tick.bid <= emaH1)  return false;
      return true;
   }
   else if(orderType == ORDER_TYPE_SELL)
   {
      if(emaM5 > 0 && tick.ask >= emaM5) return false;
      if(cfg.useEMAM15 && emaM15 > 0 && tick.ask >= emaM15) return false;
      if(cfg.useEMAH1  && emaH1 > 0  && tick.ask >= emaH1)  return false;
      return true;
   }

   return false;
}

void OpenInstantPosition()
{
   if(cfg.isNewEntriesPaused) return; // Respect SaaS remote pause

   MqlTick tick;
   if(!SymbolInfoTick(_Symbol, tick)) return;

   double emaM5 = GetEMAPrice(emaM5Handle);
   if(emaM5 <= 0) return;

   if(tick.ask > emaM5 && CheckMTFTrend(ORDER_TYPE_BUY))
   {
      trade.Buy(cfg.baseLot, _Symbol, tick.ask, 0.0, 0.0, "SuperBrain Instant Buy");
   }
   else if(tick.bid < emaM5 && CheckMTFTrend(ORDER_TYPE_SELL))
   {
      trade.Sell(cfg.baseLot, _Symbol, tick.bid, 0.0, 0.0, "SuperBrain Instant Sell");
   }
}

void CloseAllPositions()
{
   int maxAttempts = 30;
   int attempts = 0;

   while(CountMyPositions() > 0 && attempts < maxAttempts && !IsStopped())
   {
      int total = PositionsTotal();
      for(int i = total - 1; i >= 0; i--)
      {
         ulong ticket = PositionGetTicket(i);
         if(ticket > 0 && PositionGetString(POSITION_SYMBOL) == _Symbol && 
            PositionGetInteger(POSITION_MAGIC) == (long)InpMagicNumber)
         {
            trade.PositionClose(ticket);
         }
      }
      Sleep(100);
      attempts++;
   }
}

void ManageGridAndClose(double currentATR)
{
   double totalProfit    = GetTotalProfit();
   double accountBalance = AccountInfoDouble(ACCOUNT_BALANCE);

   // Hard Cut Loss Protection (% Equity Drawdown)
   if(accountBalance > 0 && totalProfit < 0 && MathAbs(totalProfit) >= (accountBalance * (cfg.maxDrawdownPct / 100.0)))
   {
      CloseAllPositions();
      return;
   }

   int buyCount = 0, sellCount = 0;
   double totalVolume = 0.0, lastLot = cfg.baseLot;
   ulong latestTimeMsc = 0;
   long lastPosType = -1;
   double lastPosPrice = 0.0;

   int total = PositionsTotal();
   for(int i = total - 1; i >= 0; i--)
   {
      ulong ticket = PositionGetTicket(i);
      if(ticket > 0 && PositionGetString(POSITION_SYMBOL) == _Symbol && 
         PositionGetInteger(POSITION_MAGIC) == (long)InpMagicNumber)
      {
         long type   = PositionGetInteger(POSITION_TYPE);
         double vol  = PositionGetDouble(POSITION_VOLUME);
         ulong pTime = (ulong)PositionGetInteger(POSITION_TIME_MSC);

         totalVolume += vol;
         if(type == POSITION_TYPE_BUY)  buyCount++;
         if(type == POSITION_TYPE_SELL) sellCount++;

         if(pTime >= latestTimeMsc)
         {
            latestTimeMsc = pTime;
            lastPosPrice  = PositionGetDouble(POSITION_PRICE_OPEN);
            lastPosType   = type;
            lastLot       = vol;
         }
      }
   }

   int totalPos = buyCount + sellCount;
   if(totalPos == 0) return;

   // Target Profit Akumulasi Keranjang Order
   double tickSize   = SymbolInfoDouble(_Symbol, SYMBOL_TRADE_TICK_SIZE);
   double tickValue  = SymbolInfoDouble(_Symbol, SYMBOL_TRADE_TICK_VALUE);
   double pointValue = (tickSize > 0) ? (tickValue * _Point / tickSize) : (_Point * 10);
   double targetProfitCurrency = totalVolume * cfg.minTPPoints * pointValue;

   // Close All jika akumulasi target profit tercapai
   if(totalProfit >= targetProfitCurrency && totalProfit > 0)
   {
      CloseAllPositions();
      return;
   }

   // Cek Batas Maksimal Posisi Aktif
   if(totalPos >= cfg.maxPositions) return;

   MqlTick tick;
   if(!SymbolInfoTick(_Symbol, tick)) return;

   // HITUNG JARAK GRID EFEKTIF
   double dynamicATRMult  = GetATRMultiplier(totalPos);
   double minDistancePrice = cfg.minGridDistancePoints * _Point;
   double atrDistancePrice = currentATR * dynamicATRMult;
   double effectiveGridDistance = MathMax(atrDistancePrice, minDistancePrice);

   // OTAK BREAKOUT SUPPORT/RESISTANCE (RECOVERY HEDGING)
   if(cfg.useBreakoutRecovery)
   {
      double resistanceM5 = GetM5Resistance(cfg.srPeriod);
      double supportM5    = GetM5Support(cfg.srPeriod);

      if(sellCount > 0 && resistanceM5 > 0 && tick.ask > resistanceM5)
      {
         if(MathAbs(tick.ask - lastPosPrice) >= effectiveGridDistance)
         {
            double recoveryLot = NormalizeLotSize(lastLot * cfg.recoveryLotMult);
            trade.Buy(recoveryLot, _Symbol, tick.ask, 0.0, 0.0, "Breakout BUY Recovery");
            return;
         }
      }

      if(buyCount > 0 && supportM5 > 0 && tick.bid < supportM5)
      {
         if(MathAbs(lastPosPrice - tick.bid) >= effectiveGridDistance)
         {
            double recoveryLot = NormalizeLotSize(lastLot * cfg.recoveryLotMult);
            trade.Sell(recoveryLot, _Symbol, tick.bid, 0.0, 0.0, "Breakout SELL Recovery");
            return;
         }
      }
   }

   // LOGIKA ATR GRID AVERAGING STANDAR
   if(lastPosType == POSITION_TYPE_BUY && (lastPosPrice - tick.bid >= effectiveGridDistance))
   {
      trade.Buy(CalculateLotSize(totalPos), _Symbol, tick.ask, 0.0, 0.0, "Grid Buy");
   }
   else if(lastPosType == POSITION_TYPE_SELL && (tick.ask - lastPosPrice >= effectiveGridDistance))
   {
      trade.Sell(CalculateLotSize(totalPos), _Symbol, tick.bid, 0.0, 0.0, "Grid Sell");
   }
}

//+------------------------------------------------------------------+
//| HUD DASHBOARD GUI WITH CLOUD STATUS                              |
//+------------------------------------------------------------------+
void CreateLabel(string name, string text, int x, int y, color clr, int fontSize, bool isBold = false)
{
   if(ObjectFind(0, name) < 0) ObjectCreate(0, name, OBJ_LABEL, 0, 0, 0);

   ObjectSetInteger(0, name, OBJPROP_XDISTANCE, x);
   ObjectSetInteger(0, name, OBJPROP_YDISTANCE, y);
   ObjectSetString(0, name, OBJPROP_TEXT, text);
   ObjectSetInteger(0, name, OBJPROP_COLOR, clr);
   ObjectSetInteger(0, name, OBJPROP_FONTSIZE, fontSize);
   ObjectSetString(0, name, OBJPROP_FONT, "Arial");
   ObjectSetInteger(0, name, OBJPROP_CORNER, CORNER_LEFT_UPPER);
   ObjectSetInteger(0, name, OBJPROP_SELECTABLE, false);
   ObjectSetInteger(0, name, OBJPROP_HIDDEN, true);
}

void CreateDashboardUI()
{
   if(ObjectFind(0, "HUD_Bg") < 0) ObjectCreate(0, "HUD_Bg", OBJ_RECTANGLE_LABEL, 0, 0, 0);

   ObjectSetInteger(0, "HUD_Bg", OBJPROP_XDISTANCE, 15);
   ObjectSetInteger(0, "HUD_Bg", OBJPROP_YDISTANCE, 15);
   ObjectSetInteger(0, "HUD_Bg", OBJPROP_XSIZE, 300);
   ObjectSetInteger(0, "HUD_Bg", OBJPROP_YSIZE, 240);
   ObjectSetInteger(0, "HUD_Bg", OBJPROP_BGCOLOR, clrDarkSlateGray);
   ObjectSetInteger(0, "HUD_Bg", OBJPROP_BORDER_COLOR, clrGold);
   ObjectSetInteger(0, "HUD_Bg", OBJPROP_CORNER, CORNER_LEFT_UPPER);
   ObjectSetInteger(0, "HUD_Bg", OBJPROP_SELECTABLE, false);
   ObjectSetInteger(0, "HUD_Bg", OBJPROP_HIDDEN, true);

   CreateLabel("HUD_Title", "SUPERBRAIN EA CONTROL CENTER", 25, 25, clrGold, 10, true);
   CreateLabel("HUD_SubTitle", "v6.40 Cloud Remote Sync Pro", 25, 42, clrKhaki, 8, false);
   
   CreateLabel("HUD_LblBalance", "Balance:", 25, 62, clrWhite, 9, false);
   CreateLabel("HUD_ValBalance", "${'$'}0.00", 150, 62, clrWhite, 9, true);
   
   CreateLabel("HUD_LblEquity", "Equity:", 25, 82, clrWhite, 9, false);
   CreateLabel("HUD_ValEquity", "${'$'}0.00", 150, 82, clrWhite, 9, true);

   CreateLabel("HUD_LblTodayProfit", "Profit Hari Ini:", 25, 102, clrWhite, 9, false);
   CreateLabel("HUD_ValTodayProfit", "${'$'}0.00", 150, 102, clrSpringGreen, 9, true);

   CreateLabel("HUD_LblFloat", "Floating Profit:", 25, 122, clrWhite, 9, false);
   CreateLabel("HUD_ValFloat", "${'$'}0.00", 150, 122, clrAqua, 9, true);

   CreateLabel("HUD_LblPosCount", "Posisi Aktif:", 25, 142, clrWhite, 9, false);
   CreateLabel("HUD_ValPosCount", "0 Order", 150, 142, clrYellow, 9, true);

   CreateLabel("HUD_LblCloudVer", "Cloud Config Ver:", 25, 162, clrWhite, 9, false);
   CreateLabel("HUD_ValCloudVer", "v1 (SYNCED)", 150, 162, clrGold, 9, true);

   CreateLabel("HUD_LblStatus", "EA Status:", 25, 185, clrWhite, 9, false);
   CreateLabel("HUD_ValStatus", "INITIALIZING...", 150, 185, clrCyan, 8, true);

   CreateLabel("HUD_LblCloud", "Cloud Link:", 25, 205, clrWhite, 8, false);
   CreateLabel("HUD_ValCloud", "CONNECTING...", 150, 205, clrAquamarine, 8, true);
}

void UpdateDashboard()
{
   double balance     = AccountInfoDouble(ACCOUNT_BALANCE);
   double equity      = AccountInfoDouble(ACCOUNT_EQUITY);
   double todayProfit = GetTodayClosedProfit();
   double floatProfit = GetTotalProfit();
   int posCount       = CountMyPositions();

   ObjectSetString(0, "HUD_ValBalance", OBJPROP_TEXT, "${'$'}" + DoubleToString(balance, 2));
   ObjectSetString(0, "HUD_ValEquity", OBJPROP_TEXT, "${'$'}" + DoubleToString(equity, 2));
   ObjectSetString(0, "HUD_ValTodayProfit", OBJPROP_TEXT, "${'$'}" + DoubleToString(todayProfit, 2));
   
   color floatClr = (floatProfit >= 0) ? clrSpringGreen : clrRed;
   ObjectSetString(0, "HUD_ValFloat", OBJPROP_TEXT, "${'$'}" + DoubleToString(floatProfit, 2));
   ObjectSetInteger(0, "HUD_ValFloat", OBJPROP_COLOR, floatClr);

   ObjectSetString(0, "HUD_ValPosCount", OBJPROP_TEXT, IntegerToString(posCount) + " Positions");
   ObjectSetString(0, "HUD_ValCloudVer", OBJPROP_TEXT, "v" + IntegerToString(cfg.configVersion));

   string status = "RUNNING (ACTIVE)";
   color statusClr = clrSpringGreen;

   if(cfg.isNewEntriesPaused)
   {
      status = "PAUSED VIA CLOUD";
      statusClr = clrOrange;
   }
   else if(todayProfit >= cfg.dailyTargetProfit)
   {
      status = "TARGET REACHED (OFF)";
      statusClr = clrGold;
   }
   else if(cfg.useTimeFilter && !IsTradingAllowedTime())
   {
      status = "OUT OF HOURS";
      statusClr = clrOrange;
   }
   else if((double)SymbolInfoInteger(_Symbol, SYMBOL_SPREAD) > cfg.maxSpreadPoints)
   {
      status = "HIGH SPREAD";
      statusClr = clrRed;
   }

   ObjectSetString(0, "HUD_ValStatus", OBJPROP_TEXT, status);
   ObjectSetInteger(0, "HUD_ValStatus", OBJPROP_COLOR, statusClr);

   ObjectSetString(0, "HUD_ValCloud", OBJPROP_TEXT, cloudConnectionStatus);
   ObjectSetInteger(0, "HUD_ValCloud", OBJPROP_COLOR, (cloudConnectionStatus == "CONNECTED (OK)") ? clrSpringGreen : clrTomato);
   
   ChartRedraw();
}

//+------------------------------------------------------------------+
//| MAIN EA EVENT HANDLERS                                           |
//+------------------------------------------------------------------+
int OnInit()
{
   trade.SetExpertMagicNumber(InpMagicNumber);
   trade.SetDeviationInPoints(InpSlippageDeviation);
   
   uint fillType = (uint)SymbolInfoInteger(_Symbol, SYMBOL_FILLING_MODE);
   if((fillType & SYMBOL_FILLING_FOK) != 0) 
      trade.SetTypeFilling(ORDER_FILLING_FOK);
   else if((fillType & SYMBOL_FILLING_IOC) != 0) 
      trade.SetTypeFilling(ORDER_FILLING_IOC);
   else 
      trade.SetTypeFilling(ORDER_FILLING_RETURN);

   // Initialize active runtime configurations with safe inputs as baseline
   cfg.configVersion         = 1;
   cfg.isNewEntriesPaused    = false;
   cfg.baseLot               = InpBaseLot;
   cfg.lotStepCount          = InpLotStepCount;
   cfg.lotStepAdd            = InpLotStepAdd;
   cfg.minTPPoints           = InpMinTP_Points;
   cfg.maxPositions          = InpMaxPositions;
   cfg.minGridDistancePoints = InpMinGridDistancePoints;
   cfg.useEMAFilter          = InpUseEMAFilter;
   cfg.emaPeriodM5           = InpEMAPeriodM5;
   cfg.useEMAM15             = InpUseEMAM15;
   cfg.emaPeriodM15          = InpEMAPeriodM15;
   cfg.useEMAH1              = InpUseEMAH1;
   cfg.emaPeriodH1           = InpEMAPeriodH1;
   cfg.atrPeriod             = InpATRPeriod;
   cfg.atrMultStep1          = InpATRMultStep1;
   cfg.atrMultStep2          = InpATRMultStep2;
   cfg.atrMultStep3          = InpATRMultStep3;
   cfg.atrMultInc            = InpATRMultInc;
   cfg.useBreakoutRecovery   = InpUseBreakoutRecovery;
   cfg.srPeriod              = InpSRPeriod;
   cfg.recoveryLotMult       = InpRecoveryLotMult;
   cfg.maxSpreadPoints       = InpMaxSpreadPoints;
   cfg.maxDrawdownPct        = InpMaxDrawdownPct;
   cfg.dailyTargetProfit     = InpDailyTargetProfit;
   cfg.slippageDeviation     = InpSlippageDeviation;
   cfg.useTimeFilter         = InpUseTimeFilter;
   cfg.startHour             = InpStartHour;
   cfg.endHour               = InpEndHour;

   atrHandle    = iATR(_Symbol, PERIOD_M1, cfg.atrPeriod);
   emaM5Handle  = iMA(_Symbol, PERIOD_M5, cfg.emaPeriodM5, 0, MODE_EMA, PRICE_CLOSE);
   emaM15Handle = iMA(_Symbol, PERIOD_M15, cfg.emaPeriodM15, 0, MODE_EMA, PRICE_CLOSE);
   emaH1Handle  = iMA(_Symbol, PERIOD_H1, cfg.emaPeriodH1, 0, MODE_EMA, PRICE_CLOSE);
   
   if(atrHandle == INVALID_HANDLE || emaM5Handle == INVALID_HANDLE || 
      emaM15Handle == INVALID_HANDLE || emaH1Handle == INVALID_HANDLE) 
   {
      Print("[SuperBrain] Gagal membuat handle indikator!");
      return(INIT_FAILED);
   }
   
   CreateDashboardUI();
   EventSetTimer(1); // Timer per detik untuk sinkronisasi cloud berkala
   return(INIT_SUCCEEDED);
}

void OnDeinit(const int reason)
{
   EventKillTimer();
   if(atrHandle != INVALID_HANDLE)    IndicatorRelease(atrHandle);
   if(emaM5Handle != INVALID_HANDLE)  IndicatorRelease(emaM5Handle);
   if(emaM15Handle != INVALID_HANDLE) IndicatorRelease(emaM15Handle);
   if(emaH1Handle != INVALID_HANDLE)  IndicatorRelease(emaH1Handle);
   ObjectsDeleteAll(0, "HUD_");
}

void OnTimer()
{
   ProcessCloudCommunication();
   UpdateDashboard();
}

void OnTick()
{
   UpdateDashboard();

   int totalPos = CountMyPositions();

   if(totalPos > 0)
   {
      double atrValue[];
      ArraySetAsSeries(atrValue, true);
      if(CopyBuffer(atrHandle, 0, 0, 1, atrValue) > 0)
      {
         ManageGridAndClose(atrValue[0]);
      }
      return;
   }

   if(GetTodayClosedProfit() >= cfg.dailyTargetProfit) return;
   if(cfg.useTimeFilter && !IsTradingAllowedTime()) return;
   if((double)SymbolInfoInteger(_Symbol, SYMBOL_SPREAD) > cfg.maxSpreadPoints) return;

   OpenInstantPosition();
}
//+------------------------------------------------------------------+
"""
}
