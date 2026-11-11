"""Schémas Pydantic des voyageurs."""

from datetime import date

from pydantic import BaseModel, ConfigDict, Field


class VoyageurCreate(BaseModel):
    nom: str = Field(min_length=1, max_length=100)
    prenom: str = Field(min_length=1, max_length=100)
    date_naissance: date
    numero_piece_identite: str = Field(min_length=1, max_length=100)


class VoyageurRead(VoyageurCreate):
    model_config = ConfigDict(from_attributes=True)

    id_voyageur: int
    id_reservation: int
