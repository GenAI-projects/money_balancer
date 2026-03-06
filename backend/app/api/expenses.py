from decimal import Decimal, ROUND_HALF_UP

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.orm import Session, joinedload

from app.db.session import get_db
from app.models.models import Expense, ExpenseSplit, Group, GroupMember, User
from app.schemas.expense import ExpenseCreate, ExpenseOut

router = APIRouter(prefix="/groups/{group_id}/expenses", tags=["expenses"])


@router.get("", response_model=list[ExpenseOut])
def list_expenses(group_id: int, db: Session = Depends(get_db)) -> list[Expense]:
    expenses = db.scalars(
        select(Expense)
        .where(Expense.group_id == group_id)
        .options(joinedload(Expense.splits).joinedload(ExpenseSplit.user))
        .order_by(Expense.id)
    ).all()
    return expenses


@router.post("", response_model=ExpenseOut, status_code=status.HTTP_201_CREATED)
def create_expense(group_id: int, payload: ExpenseCreate, db: Session = Depends(get_db)) -> Expense:
    group = db.scalar(select(Group).where(Group.id == group_id))
    if not group:
        raise HTTPException(status_code=404, detail="Group not found")

    members = db.scalars(select(GroupMember).where(GroupMember.group_id == group_id)).all()
    member_user_ids = {m.user_id for m in members}

    if payload.paid_by_user_id not in member_user_ids:
        raise HTTPException(status_code=400, detail="Payer is not in group")

    if not set(payload.split_user_ids).issubset(member_user_ids):
        raise HTTPException(status_code=400, detail="Split users must all be members of the group")

    payer = db.scalar(select(User).where(User.id == payload.paid_by_user_id))
    if not payer:
        raise HTTPException(status_code=400, detail="Payer user does not exist")

    amount = Decimal(payload.amount)
    split_count = Decimal(len(payload.split_user_ids))
    per_user = (amount / split_count).quantize(Decimal("0.01"), rounding=ROUND_HALF_UP)

    expense = Expense(
        group_id=group_id,
        title=payload.title,
        amount=amount,
        currency=payload.currency.upper(),
        paid_by_user_id=payload.paid_by_user_id,
        category=payload.category,
        note=payload.note,
        recurring_days=payload.recurring_days,
    )
    db.add(expense)
    db.flush()

    splits: list[ExpenseSplit] = []
    for uid in payload.split_user_ids:
        split = ExpenseSplit(expense_id=expense.id, user_id=uid, share_amount=per_user)
        db.add(split)
        splits.append(split)

    # Fix rounding drift by adjusting last split.
    total_split = per_user * len(payload.split_user_ids)
    drift = amount - total_split
    if drift != 0 and splits:
        splits[-1].share_amount = (Decimal(splits[-1].share_amount) + drift).quantize(Decimal("0.01"))

    db.commit()

    created = db.scalar(
        select(Expense)
        .where(Expense.id == expense.id)
        .options(joinedload(Expense.splits).joinedload(ExpenseSplit.user))
    )
    return created
