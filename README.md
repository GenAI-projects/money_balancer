# FreeWise (Android APK-First)

FreeWise is a **free Splitwise-style expense sharing app** focused on Android first (APK distribution), with clean architecture to expand to iOS later.

## What this MVP includes

- Android app scaffold using Kotlin + Jetpack Compose.
- Group expense tracking.
- Per-member balance calculation.
- Debt simplification (minimal transactions to settle).
- Categories, recurring expense marker, and notes.
- “Insights” tab with quick metrics and roadmap extras.

## Why this can replace Splitwise over time

This version already covers the core flow:
1. Create shared group context.
2. Add and view expenses.
3. See who owes whom.
4. Settle using simplified transfers.

## Planned user-friendly upgrades

- OCR receipt scan from camera.
- UPI/Paytm/GPay deep-link settlement buttons.
- Smart reminders with snooze windows.
- Offline-first mode with conflict-safe sync.
- Multi-currency and travel trip splitting.
- Export to CSV/PDF for transparency.

## Run locally

1. Open in Android Studio Iguana+.
2. Sync Gradle.
3. Run `app` on emulator/device.
4. Build APK from **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

## Testing

Run unit tests with:

```bash
./gradlew test
```
