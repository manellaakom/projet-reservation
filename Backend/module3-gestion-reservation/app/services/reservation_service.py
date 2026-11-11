"""Logique métier des réservations."""

import uuid

from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models import Billet, DisponibiliteStatus, Reservation, ReservationStatus, Voyageur
from app.repositories import disponibilite_repository, reservation_repository
from app.schemas.reservation import ReservationConfirm, ReservationCreate


def get_or_404(db: Session, reference: str) -> Reservation:
    """Retrouve une réservation par son id numérique ou son code RES-000042."""
    raw = reference.upper().removeprefix("RES-")
    if not raw.isdigit():
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"Réservation introuvable : {reference}")
    reservation = reservation_repository.get(db, int(raw))
    if reservation is None:
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"Réservation introuvable : {reference}")
    return reservation


def create(db: Session, data: ReservationCreate) -> Reservation:
    if data.voyageurs and len(data.voyageurs) != data.nombre_voyageurs:
        raise HTTPException(
            status.HTTP_422_UNPROCESSABLE_ENTITY,
            "nombre_voyageurs ne correspond pas à la liste des voyageurs",
        )
    dispo = disponibilite_repository.get_by_voyage(db, data.id_voyage)
    if dispo is None:
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"Aucune disponibilité pour le voyage {data.id_voyage}")
    if dispo.statut != DisponibiliteStatus.DISPONIBLE or dispo.places_disponibles < data.nombre_voyageurs:
        raise HTTPException(status.HTTP_409_CONFLICT, "Pas assez de places disponibles pour ce voyage")

    dispo.places_disponibles -= data.nombre_voyageurs
    if dispo.places_disponibles == 0:
        dispo.statut = DisponibiliteStatus.COMPLETE

    reservation = Reservation(
        id_user=data.id_user,
        id_voyage=data.id_voyage,
        nombre_voyageurs=data.nombre_voyageurs,
        prix_total=data.prix_total,
        statut=ReservationStatus.EN_ATTENTE,
        voyageurs=[Voyageur(**v.model_dump()) for v in data.voyageurs],
    )
    db.add(reservation)
    db.commit()
    db.refresh(reservation)
    return reservation


def confirm(db: Session, reference: str, data: ReservationConfirm) -> Reservation:
    reservation = get_or_404(db, reference)
    if reservation.statut != ReservationStatus.EN_ATTENTE:
        raise HTTPException(status.HTTP_409_CONFLICT, f"Réservation déjà {reservation.statut.value}")
    reservation.statut = ReservationStatus.CONFIRMEE
    reservation.id_paiement = data.id_paiement
    reservation.billet = Billet(
        code_billet=f"BIL-{reservation.id_reservation:06d}-{uuid.uuid4().hex[:6].upper()}"
    )
    db.commit()
    db.refresh(reservation)
    return reservation


def cancel(db: Session, reference: str) -> Reservation:
    reservation = get_or_404(db, reference)
    if reservation.statut in (ReservationStatus.ANNULEE, ReservationStatus.EXPIREE):
        raise HTTPException(status.HTTP_409_CONFLICT, f"Réservation déjà {reservation.statut.value}")
    reservation.statut = ReservationStatus.ANNULEE
    dispo = disponibilite_repository.get_by_voyage(db, reservation.id_voyage)
    if dispo is not None:
        dispo.places_disponibles = min(dispo.nombre_places, dispo.places_disponibles + reservation.nombre_voyageurs)
        if dispo.places_disponibles > 0 and dispo.statut == DisponibiliteStatus.COMPLETE:
            dispo.statut = DisponibiliteStatus.DISPONIBLE
    db.commit()
    db.refresh(reservation)
    return reservation
