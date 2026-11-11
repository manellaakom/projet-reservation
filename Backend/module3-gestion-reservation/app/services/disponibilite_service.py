"""Logique métier des disponibilités."""

from fastapi import HTTPException, status
from sqlalchemy.orm import Session

from app.models import Disponibilite, DisponibiliteStatus
from app.repositories import disponibilite_repository as repo
from app.schemas.disponibilite import DisponibiliteCreate, DisponibiliteUpdate


def get_for_voyage(db: Session, id_voyage: int) -> Disponibilite:
    dispo = repo.get_by_voyage(db, id_voyage)
    if dispo is None:
        raise HTTPException(status.HTTP_404_NOT_FOUND, f"Aucune disponibilité pour le voyage {id_voyage}")
    return dispo


def create(db: Session, data: DisponibiliteCreate) -> Disponibilite:
    if repo.get_by_voyage(db, data.id_voyage) is not None:
        raise HTTPException(status.HTTP_409_CONFLICT, "Une disponibilité existe déjà pour ce voyage")
    dispo = Disponibilite(
        id_voyage=data.id_voyage,
        nombre_places=data.nombre_places,
        places_disponibles=data.nombre_places,
        statut=DisponibiliteStatus.DISPONIBLE if data.nombre_places > 0 else DisponibiliteStatus.COMPLETE,
    )
    db.add(dispo)
    db.commit()
    db.refresh(dispo)
    return dispo


def update(db: Session, id_voyage: int, data: DisponibiliteUpdate) -> Disponibilite:
    dispo = get_for_voyage(db, id_voyage)
    changes = data.model_dump(exclude_unset=True)
    for field, value in changes.items():
        if value is not None:
            setattr(dispo, field, value)
    if dispo.places_disponibles > dispo.nombre_places:
        db.rollback()
        raise HTTPException(status.HTTP_422_UNPROCESSABLE_ENTITY, "places_disponibles > nombre_places")
    if changes.get("statut") is None:
        dispo.statut = (
            DisponibiliteStatus.COMPLETE if dispo.places_disponibles == 0 else DisponibiliteStatus.DISPONIBLE
        )
    db.commit()
    db.refresh(dispo)
    return dispo


def seed_defaults(db: Session, nb_voyages: int = 20, places: int = 100) -> int:
    """Initialise des disponibilités pour les voyages du catalogue (Module 2) si la table est vide."""
    if repo.list_all(db):
        return 0
    for id_voyage in range(1, nb_voyages + 1):
        db.add(Disponibilite(id_voyage=id_voyage, nombre_places=places, places_disponibles=places))
    db.commit()
    return nb_voyages
