from decimal import Decimal

from pydantic import BaseModel


class BalanceOut(BaseModel):
    user_id: int
    user_name: str
    balance: Decimal


class SettlementOut(BaseModel):
    from_user_id: int
    from_user_name: str
    to_user_id: int
    to_user_name: str
    amount: Decimal
