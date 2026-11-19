# Shimika (Android TV Port)

<div align="center">

[![Repository](https://img.shields.io/badge/GitHub-TheOwaisLone%2FShimika-blue?style=for-the-badge&logo=github)](https://github.com/TheOwaisLone/Shimika)
[![Platform](https://img.shields.io/badge/Platform-Android%20TV%20%7C%20Google%20TV-green?style=for-the-badge&logo=android)](https://github.com/TheOwaisLone/Shimika)
[![Build Android APK](https://img.shields.io/github/actions/workflow/status/TheOwaisLone/Shimika/apk-build.yml?branch=main&style=for-the-badge&label=build)](https://github.com/TheOwaisLone/Shimika/actions/workflows/apk-build.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-yellow.svg?style=for-the-badge)](LICENSE)

</div>

**Shimika** is an Android TV and Google TV port of Shevery, redesigned for 10-foot television screens, full D-pad remote navigation, and wireless ADB authorization without requiring a touch screen or physical mouse.

---

### Maintainers & Credits

- **Owais Lone** — Lead Android TV Developer & Port Maintainer ([GitHub](https://github.com/TheOwaisLone) | [Repository](https://github.com/TheOwaisLone/Shimika))
- **HmnDev-Tech** — Original Creator & Upstream Developer of [Shevery](https://github.com/HmnDev-Tech/shevery)
- **RikkaApps / Shizuku Team** — Shizuku API & original architecture

---

## Android TV Features & Remote Controls

- **10-Foot UI Experience**: Landscape-first layout with high-contrast glowing focus indicators, animated card scales (1.02x–1.05x), and TV safe content margins (preventing overscan clipping).
- **Full D-pad Navigation**: Every button, card, switch, tab, and dialog is accessible using a standard TV remote:
  - **DPAD_UP / DPAD_DOWN**: Navigate vertical list items, setting switches, and cards.
  - **DPAD_LEFT / DPAD_RIGHT**: Seamlessly navigate between the landscape `TvNavigationRail` and main content cards.
  - **DPAD_CENTER / ENTER**: Activate buttons, trigger actions, or toggle preferences.
  - **BACK**: Dismiss modals, exit sub-menus, or return to the main dashboard.
- **TV Side Navigation Rail**: On television screens, the UI presents an accessible vertical navigation rail on the start edge (`TvNavigationRail`) instead of cramped mobile bottom sheets.
- **Remote-Friendly Wireless ADB Pairing**: The pairing dialog automatically requests focus on the 6-digit pairing code input upon port discovery, allowing immediate typing using the Android TV on-screen keyboard (IME) or remote number pads.
- **Side-by-Side Coexistence**: Package ID `com.owais.shimika` allows Shimika to be installed alongside upstream Shizuku and Shevery.

---

## Quick Setup: Granting All Permissions (ADB)

To ensure Shimika runs in the background and can manage privileges and services without being killed by Android TV's aggressive memory management, we provide a one-click setup script [`grant_shimika.sh`](grant_shimika.sh).

### 1. Connect via ADB
Enable **Developer Options** and **Wireless Debugging** (or USB debugging) on your Android TV / Google TV. Then connect from your computer:
```bash
adb connect <TV_IP_ADDRESS>:<PORT>
# Example: adb connect 192.168.31.38:35605
```

### 2. Run the Permission Script
Make the script executable and run it:
```bash
chmod +x grant_shimika.sh

# If only one device is connected:
./grant_shimika.sh

# Or specify your TV's address explicitly:
./grant_shimika.sh <TV_IP_ADDRESS>:<PORT>
```

### What this grants:
- **Notifications & Overlay**: `POST_NOTIFICATIONS`, `SYSTEM_ALERT_WINDOW`
- **System Settings**: `WRITE_SECURE_SETTINGS`, `PACKAGE_USAGE_STATS`, `DUMP`
- **Wi-Fi & Network Discovery**: `NEARBY_WIFI_DEVICES`, `CHANGE_WIFI_MULTICAST_STATE`
- **Background Execution**: Whitelists Shimika from Android Doze / Battery Optimization (`deviceidle whitelist`), and grants `RUN_IN_BACKGROUND` and `WAKE_LOCK`.
- **Privileged APIs**: `PRIVILEGED_API` and `com.rosan.dhizuku.permission.API`.

---

## Upstream Fork Status & Inherited Features

> [!IMPORTANT]
> **Side-by-Side Installation Supported:** Thanks to its independent package ID (`com.owais.shimika`) and isolated permission namespace, you can install Shimika directly alongside other implementations.
> Upstream project reference: <https://github.com/HmnDev-Tech/shevery> and <https://github.com/RikkaApps/Shizuku>

### Core Capabilities
- **Jetpack Compose Manager UI**: Built with Material 3 Expressive components, smooth focus transitions, and high-visibility contrast.
- **Device Owner Management & Integration**: Full Device Owner management (app delegation scopes, ownership transfer, DPM-assisted ADB) inspired by and compatible with Dhizuku.
- **Security & Protection**: Master switch to require biometric or device PIN/pattern/password authentication with configurable timeouts and granular action protection.
- **Comput & Commandium**: Shell/ADB-based "Comput" feature with command creation, macros, and diagnostics.
- **ADB Modules System**: Modules screen for installing and managing ZIP modules (`module.prop`, `action.sh`, `service.sh`, local WebUI).

---

## Documentation

- [Comput AI Agent guide](docs/comput-agent.md)
- [Device Owner & Profile Owner guide](docs/device-owner.md)
- [Stealth Edition & Compatibility Stubs](docs/stealth-and-stubs.md)
- [Accessibility Manager, USB Debugging Hiding & Network Firewall](docs/lab-and-extras.md)
- [Tasker & MacroDroid Automation Guide](docs/tasker-plugin.md)
- [Intents Reference](docs/intents.md)
- [ADB Modules guide](docs/adb-modules-guide.md)
- [ADB Modules API reference](docs/adb-modules-api.md)
- [ADB Modules publishing & GitHub Catalog guide](docs/github-catalog.md)
- [Shizuku Connectors API](docs/shizuku-connectors.md)
- [Android 17 Compatibility](docs/android-17-compatibility.md)
- [ErrorProtect Watchdog & Starter Recovery](docs/errorprotect.md)
- [Original Shevery Upstream Wiki](https://github.com/HmnDev-Tech/shevery/wiki)

---

## How Shimika Works

The core of Shimika provides an IPC middleman service that receives binder requests from authorized client applications, forwards them with elevated privileges to the Android system server, and returns the results. 

Client apps communicate via AIDL (`moe.shizuku.api.ShizukuBinderWrapper` and `rikka.shizuku.server.ShizukuService`).

---

## Developing & Building Shimika

### Build Requirements
- Android SDK 37 (Build-Tools 37.0.0, NDK `29.0.14206865`)
- JDK 21
- Rust stable toolchain with targets:
  - `aarch64-linux-android`
  - `armv7-linux-androideabi`
  - `x86_64-linux-android`
  - `i686-linux-android`
- `cargo-ndk`

### Local Compilation
```bash
# Clone with recursive submodules
git clone --recurse-submodules https://github.com/TheOwaisLone/Shimika.git
cd Shimika

# Assemble debug APK
./gradlew :manager:assembleDebug

# Assemble release APK
./gradlew :manager:assembleRelease
```

### CI/CD Builds

Automated builds run on pushes and pull requests via GitHub Actions:

| Artifact | Gradle Task | Description |
| --- | --- | --- |
| `Shimika-Android-TV-debug` | `:manager:assembleDebug` | Debuggable Android TV APK for remote testing & ADB diagnostics |
| `Shimika-Android-TV-release` | `:manager:assembleRelease` | Release Android TV APK, signed and verified with `apksigner` |

Fresh builds are available under [GitHub Actions](https://github.com/TheOwaisLone/Shimika/actions/workflows/apk-build.yml).

---

## License

All code in this project is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for complete details.

## Credits & Upstream Attribution

* **Owais Lone** — Android TV Port maintainer and developer.
* **HmnDev-Tech** — Original creator and upstream developer of Shevery.
* **RikkaApps / Shizuku Team** — Shizuku API and core sources.
* **kerneldroid** — For App UI components and catalog modules system.
* **iamr0s/Dhizuku** — For Dhizuku Device Owner architecture and API concepts.
* **Landon Moran** — For Dhizuku and TCP fixes.
* **DP-Hridayan** — For parts of "Comput" UI.
* **protonpony/Shizuku-Keeper** — For wireless debugging autostart concepts.
