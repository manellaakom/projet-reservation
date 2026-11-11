"""Endpoints des voyageurs d'une réservation."""

from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.schemas.voyageur import VoyageurRead
from app.services import voyageur_service

router = APIRouter(prefix="/api/v1/voyageurs", tags=["voyageurs"])


@router.get("/reservation/{reference}", response_model=list[VoyageurRead])
def list_voyageurs(reference: str, db: Session = Depends(get_db)):
    return voyageur_service.list_for_reservation(db, reference)
