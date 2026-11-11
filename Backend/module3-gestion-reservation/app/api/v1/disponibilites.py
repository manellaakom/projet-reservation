"""Endpoints des disponibilités de voyages."""

from fastapi import APIRouter, Depends, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.repositories import disponibilite_repository
from app.schemas.disponibilite import DisponibiliteCreate, DisponibiliteRead, DisponibiliteUpdate
from app.services import disponibilite_service

router = APIRouter(prefix="/api/v1/disponibilites", tags=["disponibilites"])


@router.get("", response_model=list[DisponibiliteRead])
def list_disponibilites(db: Session = Depends(get_db)):
    return disponibilite_repository.list_all(db)


@router.get("/voyage/{id_voyage}", response_model=DisponibiliteRead)
def get_disponibilite(id_voyage: int, db: Session = Depends(get_db)):
    return disponibilite_service.get_for_voyage(db, id_voyage)


@router.post("", response_model=DisponibiliteRead, status_code=status.HTTP_201_CREATED)
def create_disponibilite(data: DisponibiliteCreate, db: Session = Depends(get_db)):
    return disponibilite_service.create(db, data)


@router.put("/voyage/{id_voyage}", response_model=DisponibiliteRead)
def update_disponibilite(id_voyage: int, data: DisponibiliteUpdate, db: Session = Depends(get_db)):
    return disponibilite_service.update(db, id_voyage, data)
