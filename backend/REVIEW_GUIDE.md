# Backend Review Guide (5-minute path)

If 30 files feels overwhelming, use this order.

## 1) First verify what was added (high-level only)
- `backend/` was added as a new FastAPI service.
- Root `docker-compose.yml` runs Postgres + backend.
- Existing Android/web preview files were not refactored.

## 2) Read only these 4 files first
1. `backend/README.md`  
   Quick local run/test instructions.
2. `backend/app/main.py`  
   App startup + route registration + `/health`.
3. `backend/app/models/models.py`  
   Database tables and relationships.
4. `backend/tests/test_expenses_and_settlements.py`  
   Main business behavior expected by tests.

If these four look right, most of the backend shape is right.

## 3) Why there are many files
This follows standard backend separation so code stays maintainable:

- `app/api/*` = HTTP routes/endpoints
- `app/models/*` = SQLAlchemy tables/entities
- `app/schemas/*` = request/response validation models
- `app/services/*` = pure business logic (settlement math)
- `app/db/*` = DB setup/session/base
- `tests/*` = behavior verification

## 4) Minimal API flow to test manually
1. `POST /api/groups` to create a group.
2. `POST /api/groups/{group_id}/members` twice to add members.
3. `POST /api/groups/{group_id}/expenses` to add one expense.
4. `GET /api/groups/{group_id}/settlements/balances` for net balances.
5. `GET /api/groups/{group_id}/settlements` for simplified payment suggestions.

## 5) Quick verification commands
From repo root:

```bash
docker compose up --build
```

Then open:
- `http://localhost:8000/docs`
- `http://localhost:8000/health`

## 6) If you want smaller PRs next time
Ask to split into these PRs:
- PR-1: project scaffolding + health endpoint
- PR-2: models + migrations
- PR-3: groups/members API
- PR-4: expenses + settlement service
- PR-5: tests + docker/docs

That keeps each review under ~5-8 files.
