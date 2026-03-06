# FreeWise Web Preview

This folder is a browser-testable version of FreeWise for quick localhost validation.

## Requirements

- Python 3 (or any static file server)
- A modern browser

## Start

```bash
python -m http.server 8080
```

Open:

- http://localhost:8080/web_preview/

## Feature parity with Android MVP

- Dashboard with per-member balances.
- Expenses list with categories, notes, recurring marker.
- Simplified settlement suggestions.
- Insights metrics.
- Add Expense form (updates balances and settlements instantly).
