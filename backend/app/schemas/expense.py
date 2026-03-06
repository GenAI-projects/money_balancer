from datetime import datetime
from decimal import Decimal

from pydantic import BaseModel, Field

from app.models.models import ExpenseCategory
from app.schemas.group import UserOut


class ExpenseCreate(BaseModel):
    title: str = Field(min_length=1, max_length=255)
    amount: Decimal = Field(gt=0)
    currency: str = Field(default="USD", min_length=3, max_length=3)
    paid_by_user_id: int
    split_user_ids: list[int] = Field(min_length=1)
    category: ExpenseCategory = ExpenseCategory.OTHER
    note: str = ""
    recurring_days: int | None = None


class ExpenseSplitOut(BaseModel):
    id: int
    share_amount: Decimal
    user: UserOut

    model_config = {"from_attributes": True}


class ExpenseOut(BaseModel):
    id: int
    group_id: int
    title: str
    amount: Decimal
    currency: str
    paid_by_user_id: int
    category: ExpenseCategory
    note: str
    recurring_days: int | None
    created_at: datetime
    splits: list[ExpenseSplitOut]

    model_config = {"from_attributes": True}
