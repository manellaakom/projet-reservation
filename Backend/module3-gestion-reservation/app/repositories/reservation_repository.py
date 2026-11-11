"""Accès aux données des réservations."""

from sqlalchemy import select
from sqlalchemy.orm import Session

from app.models import Reservation


def get(db: Session, id_reservation: int) -> Reservation | None:
    return db.get(Reservation, id_reservation)


def list_all(db: Session, id_user: int | None = None) -> list[Reservation]:
    query = select(Reservation).order_by(Reservation.id_reservation.desc())
    if id_user is not None:
        query = query.where(Reservation.id_user == id_user)
    return list(db.scalars(query))
