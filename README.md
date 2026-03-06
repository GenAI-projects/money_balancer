# FreeWise (Android APK-First + Localhost Web Preview)

FreeWise is a **free Splitwise-style expense sharing app** focused on Android first (APK distribution), with a quick localhost web preview for testing without Android Studio Iguana+.

## What this repo includes

### 1) Android app (primary target)
- Kotlin + Jetpack Compose scaffold.
- Group expense tracking, balances, and debt simplification.
- Categories, notes, and recurring-expense metadata.

### 2) Web preview (quick local testing)
- Plain HTML/CSS/JS app in `web_preview/`.
- Mirrors core flows: dashboard, expenses, settle, insights, and add-expense form.
- Runs on any local static server.

## Run the localhost web preview

From the repo root:

```bash
python -m http.server 8080
```

Then open:

- `http://localhost:8080/web_preview/`

## Run Android app locally

1. Open in Android Studio Iguana+.
2. Sync Gradle.
3. Run `app` on emulator/device.
4. Build APK from **Build > Build Bundle(s) / APK(s) > Build APK(s)**.

## Planned user-friendly upgrades

- OCR receipt scan from camera.
- Payment deep-link settlement buttons.
- Smart reminders with snooze windows.
- Offline-first mode with conflict-safe sync.
- Multi-currency and travel trip splitting.
- Export to CSV/PDF for transparency.
