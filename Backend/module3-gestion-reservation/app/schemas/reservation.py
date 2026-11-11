"""Schémas Pydantic des réservations."""

from datetime import datetime
from decimal import Decimal

from pydantic import BaseModel, ConfigDict, Field, computed_field

from app.models.enums import ReservationStatus
from app.schemas.voyageur import VoyageurCreate, VoyageurRead


class ReservationCreate(BaseModel):
    id_user: int = Field(ge=1)
    id_voyage: int = Field(ge=1)
    nombre_voyageurs: int = Field(ge=1)
    prix_total: Decimal = Field(ge=0)
    voyageurs: list[VoyageurCreate] = Field(default_factory=list)


class ReservationConfirm(BaseModel):
    id_paiement: int = Field(ge=1)


class BilletRead(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id_billet: int
    code_billet: str
    date_emission: datetime


class ReservationRead(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id_reservation: int
    id_user: int
    id_voyage: int
    id_paiement: int | None = None
    date_reservation: datetime
    statut: ReservationStatus
    nombre_voyageurs: int
    prix_total: Decimal
    voyageurs: list[VoyageurRead] = Field(default_factory=list)
    billet: BilletRead | None = None

    # Champs de compatibilité avec le DTO Feign du Module 1 (ReservationDto)
    @computed_field  # type: ignore[prop-decorator]
    @property
    def id(self) -> str:
        return str(self.id_reservation)

    @computed_field  # type: ignore[prop-decorator]
    @property
    def codeReservation(self) -> str:  # noqa: N802
        return f"RES-{self.id_reservation:06d}"

    @computed_field  # type: ignore[prop-decorator]
    @property
    def voyageurNom(self) -> str | None:  # noqa: N802
        if not self.voyageurs:
            return None
        v = self.voyageurs[0]
        return f"{v.prenom} {v.nom}"

    @computed_field  # type: ignore[prop-decorator]
    @property
    def valide(self) -> bool:
        return self.statut in (ReservationStatus.EN_ATTENTE, ReservationStatus.CONFIRMEE)
