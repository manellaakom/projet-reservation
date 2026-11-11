"""Accès aux données des disponibilités."""

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models import Disponibilite


def get_by_voyage(db: Session, id_voyage: int) -> Disponibilite | None:
    return db.scalar(select(Disponibilite).where(Disponibilite.id_voyage == id_voyage))


def list_all(db: Session) -> list[Disponibilite]:
    return list(db.scalars(select(Disponibilite).order_by(Disponibilite.id_voyage)))
