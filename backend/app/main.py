from fastapi import FastAPI
from fastapi.responses import HTMLResponse
from fastapi.staticfiles import StaticFiles
from fastapi.templating import Jinja2Templates
from starlette.requests import Request

from app.api.expenses import router as expenses_router
from app.api.groups import router as groups_router
from app.api.settlements import router as settlements_router
from app.core.config import settings
from app.db.base import Base
from app.db.session import engine

app = FastAPI(title=settings.app_name)

Base.metadata.create_all(bind=engine)

app.include_router(groups_router, prefix="/api")
app.include_router(expenses_router, prefix="/api")
app.include_router(settlements_router, prefix="/api")

app.mount("/static", StaticFiles(directory="app/static"), name="static")
templates = Jinja2Templates(directory="app/templates")


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.get("/", response_class=HTMLResponse)
def index(request: Request):
    return templates.TemplateResponse("index.html", {"request": request})
