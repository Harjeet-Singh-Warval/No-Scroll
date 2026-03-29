# FocusGuard \ud83d\udee1\ufe0f

FocusGuard is a native Android application designed to intelligently block short-form videos (Reels, Shorts, TikToks) without entirely blocking the host applications. It uses Android's Accessibility Services to detect and intercept content in real-time.

## Features
- **Smart Detection**: Detects short-form videos on Instagram, YouTube, Facebook, Snapchat, and TikTok.
- **Selective Blocking**: Blocks only the short-video tabs/feeds while allowing normal app functionality (e.g., DMs, long-form videos, regular posts).
- **Curious Mode**: Allows you to watch a configurable number of short videos before blocking.
- **Break Timer**: Pause blocking temporarily (5, 10, 15, or 30 minutes).
- **Local Analytics**: Complete privacy with local tracking of blocks and time saved.
- **Material 3 Design**: Clean, modern dark-mode aesthetic.

## Setup Instructions

### Prerequisites
- Android Studio Ladybug or newer.
- JDK 17 (or 11 if configured).
- Minimum API 23 (Android 6.0), target API 34.

### Build and Run
1. Open the project in Android Studio.
2. Allow Gradle to sync dependencies (Room, DataStore, Hilt, Compose, Navigation, MPAndroidChart).
3. Build the project using:
   ```bash
   ./gradlew assembleDebug
   ```
4. Install on your device:
   ```bash
   ./gradlew installDebug
   ```

## OEM Battery Optimization Guide
Due to aggressive battery-saving implementations by various OEMs (Samsung, Xiaomi, OnePlus), Background / Accessibility Services may be killed unexpectedly. 

To ensure FocusGuard remains active:
1. **Samsung**:
   - Go to Settings > Battery > Background usage limits > Never sleeping apps.
   - Add `FocusGuard` to the list.
2. **Xiaomi / POCO / Redmi**:
   - Go to Settings > Apps > Manage apps > FocusGuard.
   - Enable "Autostart".
   - Set "Battery saver" to "No restrictions".
3. **OnePlus / Realme / OPPO**:
   - Go to Settings > Apps > App management > FocusGuard > Battery usage.
   - Enable "Allow background activity" and "Allow auto launch".

## Architecture
- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Dependency Injection**: Hilt
- **Local Database**: Room + DataStore
- **Background**: Foreground Service + Accessibility Service
- **Pattern**: Clean Architecture + MVVM

## Permissions Required
- `BIND_ACCESSIBILITY_SERVICE`: Used for the core detection engine.
- `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_SPECIAL_USE`: Used to keep the detection engine alive.
- `BIND_DEVICE_ADMIN` (Optional): Used to lock the screen instead of closing the app upon detection.

---
_Focus on what matters, guard your time._
