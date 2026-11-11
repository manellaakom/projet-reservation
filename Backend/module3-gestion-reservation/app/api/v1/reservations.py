"""Endpoints des réservations."""

from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.repositories import reservation_repository
from app.schemas.reservation import ReservationConfirm, ReservationCreate, ReservationRead
from app.services import reservation_service

router = APIRouter(prefix="/api/v1/reservations", tags=["reservations"])


@router.get("", response_model=list[ReservationRead])
def list_reservations(id_user: int | None = None, db: Session = Depends(get_db)):
    return reservation_repository.list_all(db, id_user)


@router.post("", response_model=ReservationRead, status_code=status.HTTP_201_CREATED)
def create_reservation(data: ReservationCreate, db: Session = Depends(get_db)):
    return reservation_service.create(db, data)


@router.get("/{reference}", response_model=ReservationRead)
def get_reservation(reference: str, db: Session = Depends(get_db)):
    return reservation_service.get_or_404(db, reference)


@router.put("/{reference}/confirmer", response_model=ReservationRead)
def confirm_reservation(reference: str, data: ReservationConfirm, db: Session = Depends(get_db)):
    return reservation_service.confirm(db, reference, data)


@router.patch("/{reference}/annuler", response_model=ReservationRead)
def cancel_reservation(reference: str, db: Session = Depends(get_db)):
    return reservation_service.cancel(db, reference)
