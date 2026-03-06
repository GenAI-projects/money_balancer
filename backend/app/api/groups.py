from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.orm import Session, joinedload

from app.db.session import get_db
from app.models.models import Group, GroupMember, User
from app.schemas.group import GroupCreate, GroupOut, MemberCreate

router = APIRouter(prefix="/groups", tags=["groups"])


@router.post("", response_model=GroupOut, status_code=status.HTTP_201_CREATED)
def create_group(payload: GroupCreate, db: Session = Depends(get_db)) -> Group:
    group = Group(name=payload.name, base_currency=payload.base_currency.upper())
    db.add(group)
    db.commit()
    db.refresh(group)
    return group


@router.get("", response_model=list[GroupOut])
def list_groups(db: Session = Depends(get_db)) -> list[Group]:
    groups = db.scalars(select(Group).order_by(Group.id)).all()
    return groups


@router.get("/{group_id}", response_model=GroupOut)
def get_group(group_id: int, db: Session = Depends(get_db)) -> Group:
    group = db.scalar(
        select(Group).where(Group.id == group_id).options(joinedload(Group.members).joinedload(GroupMember.user))
    )
    if not group:
        raise HTTPException(status_code=404, detail="Group not found")
    return group


@router.post("/{group_id}/members", response_model=GroupOut)
def add_member(group_id: int, payload: MemberCreate, db: Session = Depends(get_db)) -> Group:
    group = db.scalar(select(Group).where(Group.id == group_id))
    if not group:
        raise HTTPException(status_code=404, detail="Group not found")

    user = User(name=payload.name)
    db.add(user)
    db.flush()
    db.add(GroupMember(group_id=group_id, user_id=user.id))
    db.commit()

    full_group = db.scalar(
        select(Group).where(Group.id == group_id).options(joinedload(Group.members).joinedload(GroupMember.user))
    )
    return full_group
