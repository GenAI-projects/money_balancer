# FreeWise (Android APK-First + Localhost Web Preview)

FreeWise is a free Splitwise-style app focused on Android APK delivery, with a fully testable localhost web app.

## Live features now integrated

- OCR receipt scan flow (web simulation + Android receipt metadata).
- Payment deep-link settlement actions (UPI + PayPal links).
- Smart reminders with snooze.
- Offline-first behavior (web LocalStorage + JSON import/export merge by latest update).
- Multi-currency trip splitting with normalization to a base currency.
- Export transparency tools (CSV + print-to-PDF on localhost web).
- Blade Runner-inspired cyber-noir color scheme across interfaces.

## Localhost test (recommended)

```bash
python -m http.server 8080
```

Open: `http://localhost:8080/web_preview/`

## Android

1. Open in Android Studio Iguana+.
2. Sync Gradle.
3. Run app on emulator/device.
4. Build APK via **Build > Build Bundle(s) / APK(s) > Build APK(s)**.


## New scalable backend (FastAPI)

A production-oriented backend is now included under `backend/` using:
- FastAPI
- PostgreSQL
- SQLAlchemy 2.0
- Pytest-driven test suite

Quick start with Docker:

```bash
docker compose up --build
```

Then open:
- `http://localhost:8000/docs`
- `http://localhost:8000/health`

See `backend/README.md` for API flow and local development steps.
