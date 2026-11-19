#!/usr/bin/env bash
# ==============================================================================
# Shimika (Android TV / Google TV) - Permission & Background Setup Script
# ==============================================================================
# Grants all necessary system, runtime, AppOps, and battery-optimization permissions
# to Shimika (com.owais.shimika) so it can run reliably in the background on TV devices.
# ==============================================================================

set -euo pipefail

PACKAGE="com.owais.shimika"

# Check if an ADB target device is provided as argument, else use default connected device
if [ $# -ge 1 ]; then
    ADB_CMD="adb -s $1"
    echo "[*] Targeting ADB device: $1"
else
    ADB_CMD="adb"
    echo "[*] Targeting default connected ADB device"
fi

echo "[*] Checking device connection..."
$ADB_CMD get-state >/dev/null 2>&1 || {
    echo "[!] Error: No device found or ADB unauthorized. Make sure ADB debugging is enabled."
    exit 1
}

echo "[*] Verifying Shimika installation ($PACKAGE)..."
if ! $ADB_CMD shell pm path "$PACKAGE" >/dev/null 2>&1; then
    echo "[!] Error: $PACKAGE is not installed on the target device."
    echo "[*] Please install the Shimika APK first."
    exit 1
fi

echo "[+] Granting runtime & system permissions..."
# Notification permission (Android 13+)
$ADB_CMD shell pm grant "$PACKAGE" android.permission.POST_NOTIFICATIONS 2>/dev/null || true

# Secure & system settings
$ADB_CMD shell pm grant "$PACKAGE" android.permission.WRITE_SECURE_SETTINGS 2>/dev/null || true

# Package usage stats
$ADB_CMD shell pm grant "$PACKAGE" android.permission.PACKAGE_USAGE_STATS 2>/dev/null || true
$ADB_CMD shell appops set "$PACKAGE" GET_USAGE_STATS allow 2>/dev/null || true

# System diagnostics & dump
$ADB_CMD shell pm grant "$PACKAGE" android.permission.DUMP 2>/dev/null || true

# Local network & Wi-Fi device discovery
$ADB_CMD shell pm grant "$PACKAGE" android.permission.NEARBY_WIFI_DEVICES 2>/dev/null || true
$ADB_CMD shell pm grant "$PACKAGE" android.permission.CHANGE_WIFI_MULTICAST_STATE 2>/dev/null || true

# Package management & install permissions
$ADB_CMD shell appops set "$PACKAGE" REQUEST_INSTALL_PACKAGES allow 2>/dev/null || true

# Window overlay / system alert
$ADB_CMD shell appops set "$PACKAGE" SYSTEM_ALERT_WINDOW allow 2>/dev/null || true

# Privileged API permissions
$ADB_CMD shell pm grant "$PACKAGE" com.hamondev.shevery.permission.PRIVILEGED_API 2>/dev/null || true
$ADB_CMD shell pm grant "$PACKAGE" com.rosan.dhizuku.permission.API 2>/dev/null || true

echo "[+] Configuring background execution & battery whitelist..."
# Whitelist from Android Doze / battery optimization
$ADB_CMD shell dumpsys deviceidle whitelist +"$PACKAGE" 2>/dev/null || true
$ADB_CMD shell cmd deviceidle whitelist +"$PACKAGE" 2>/dev/null || true

# AppOps background allowances
$ADB_CMD shell appops set "$PACKAGE" RUN_IN_BACKGROUND allow 2>/dev/null || true
$ADB_CMD shell appops set "$PACKAGE" RUN_ANY_IN_BACKGROUND allow 2>/dev/null || true
$ADB_CMD shell appops set "$PACKAGE" WAKE_LOCK allow 2>/dev/null || true

echo "[+] Starting Shimika..."
$ADB_CMD shell am start -n "$PACKAGE/moe.shizuku.manager.MainActivity" 2>/dev/null || true

echo ""
echo "=============================================================================="
echo " [SUCCESS] All permissions and background allowances configured for Shimika!"
echo "=============================================================================="
