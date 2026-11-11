"""Logique métier des voyageurs."""

from sqlalchemy.orm import Session

from app.models import Voyageur
from app.repositories import voyageur_repository
from app.services import reservation_service


def list_for_reservation(db: Session, reference: str) -> list[Voyageur]:
    reservation = reservation_service.get_or_404(db, reference)
    return voyageur_repository.list_by_reservation(db, reservation.id_reservation)
