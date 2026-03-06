# FreeWise Backend (FastAPI + PostgreSQL + SQLAlchemy)

## Stack
- FastAPI
- SQLAlchemy 2.0
- PostgreSQL
- Alembic-ready project structure
- Pytest for TDD

## Run locally

```bash
cd backend
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
export FREEWISE_DATABASE_URL='postgresql+psycopg://postgres:postgres@localhost:5432/freewise'
uvicorn app.main:app --reload
```

Open:
- API docs: http://127.0.0.1:8000/docs
- Health: http://127.0.0.1:8000/health

## Run tests

```bash
cd backend
pytest -q
```

Tests use an isolated in-memory SQLite database.

## Minimal API flow
1. Create group: `POST /api/groups`
2. Add members: `POST /api/groups/{group_id}/members`
3. Add expense: `POST /api/groups/{group_id}/expenses`
4. View balances: `GET /api/groups/{group_id}/settlements/balances`
5. View simplified settlements: `GET /api/groups/{group_id}/settlements`
