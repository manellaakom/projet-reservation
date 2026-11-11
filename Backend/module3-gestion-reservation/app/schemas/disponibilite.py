"""Schémas Pydantic des disponibilités."""

from pydantic import BaseModel, ConfigDict, Field

from app.models.enums import DisponibiliteStatus


class DisponibiliteCreate(BaseModel):
    id_voyage: int = Field(ge=1)
    nombre_places: int = Field(ge=0)


class DisponibiliteUpdate(BaseModel):
    nombre_places: int | None = Field(default=None, ge=0)
    places_disponibles: int | None = Field(default=None, ge=0)
    statut: DisponibiliteStatus | None = None


class DisponibiliteRead(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id_disponibilite: int
    id_voyage: int
    nombre_places: int
    places_disponibles: int
    statut: DisponibiliteStatus
