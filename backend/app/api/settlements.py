from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.db.session import get_db
from app.schemas.settlement import BalanceOut, SettlementOut
from app.services.settlement_service import calculate_balances, simplify_settlements

router = APIRouter(prefix="/groups/{group_id}/settlements", tags=["settlements"])


@router.get("/balances", response_model=list[BalanceOut])
def get_balances(group_id: int, db: Session = Depends(get_db)) -> list[dict]:
    return calculate_balances(db, group_id)


@router.get("", response_model=list[SettlementOut])
def get_settlements(group_id: int, db: Session = Depends(get_db)) -> list[dict]:
    balances = calculate_balances(db, group_id)
    return simplify_settlements(balances)
