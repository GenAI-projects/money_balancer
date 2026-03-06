from __future__ import annotations

from decimal import Decimal, ROUND_HALF_UP

from sqlalchemy import select
from sqlalchemy.orm import Session, joinedload

from app.models.models import Expense, GroupMember

TWO_DP = Decimal("0.01")


def quantize(value: Decimal) -> Decimal:
    return value.quantize(TWO_DP, rounding=ROUND_HALF_UP)


def calculate_balances(db: Session, group_id: int) -> list[dict]:
    members = db.scalars(
        select(GroupMember).where(GroupMember.group_id == group_id).options(joinedload(GroupMember.user))
    ).all()
    balances = {member.user_id: Decimal("0.00") for member in members}

    expenses = db.scalars(
        select(Expense).where(Expense.group_id == group_id).options(joinedload(Expense.splits))
    ).all()

    for expense in expenses:
        balances[expense.paid_by_user_id] += Decimal(expense.amount)
        for split in expense.splits:
            balances[split.user_id] -= Decimal(split.share_amount)

    return [
        {
            "user_id": member.user_id,
            "user_name": member.user.name,
            "balance": quantize(balances[member.user_id]),
        }
        for member in members
    ]


def simplify_settlements(balances: list[dict]) -> list[dict]:
    debtors: list[dict] = []
    creditors: list[dict] = []
    for row in balances:
        value = Decimal(row["balance"])
        if value < Decimal("-0.01"):
            debtors.append({**row, "amount": -value})
        elif value > Decimal("0.01"):
            creditors.append({**row, "amount": value})

    debtors.sort(key=lambda x: x["amount"], reverse=True)
    creditors.sort(key=lambda x: x["amount"], reverse=True)

    settlements = []
    i = j = 0
    while i < len(debtors) and j < len(creditors):
        amount = min(debtors[i]["amount"], creditors[j]["amount"])
        amount = quantize(amount)
        settlements.append(
            {
                "from_user_id": debtors[i]["user_id"],
                "from_user_name": debtors[i]["user_name"],
                "to_user_id": creditors[j]["user_id"],
                "to_user_name": creditors[j]["user_name"],
                "amount": amount,
            }
        )
        debtors[i]["amount"] = quantize(debtors[i]["amount"] - amount)
        creditors[j]["amount"] = quantize(creditors[j]["amount"] - amount)

        if debtors[i]["amount"] <= Decimal("0.01"):
            i += 1
        if creditors[j]["amount"] <= Decimal("0.01"):
            j += 1

    return settlements
