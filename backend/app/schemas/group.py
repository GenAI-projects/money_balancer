from datetime import datetime

from pydantic import BaseModel, Field


class GroupCreate(BaseModel):
    name: str = Field(min_length=1, max_length=120)
    base_currency: str = Field(default="USD", min_length=3, max_length=3)


class MemberCreate(BaseModel):
    name: str = Field(min_length=1, max_length=120)


class UserOut(BaseModel):
    id: int
    name: str

    model_config = {"from_attributes": True}


class MemberOut(BaseModel):
    id: int
    user: UserOut
    joined_at: datetime

    model_config = {"from_attributes": True}


class GroupOut(BaseModel):
    id: int
    name: str
    base_currency: str
    created_at: datetime
    members: list[MemberOut] = []

    model_config = {"from_attributes": True}
