"""Accès aux données des voyageurs."""

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models import Voyageur


def list_by_reservation(db: Session, id_reservation: int) -> list[Voyageur]:
    return list(db.scalars(select(Voyageur).where(Voyageur.id_reservation == id_reservation)))
