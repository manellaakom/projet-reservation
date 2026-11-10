"""Modèle SQLAlchemy d'une réservation."""

from datetime import datetime
from decimal import Decimal

from sqlalchemy import DateTime, Enum, Integer, Numeric, func
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.database import Base
from app.models.enums import ReservationStatus


class Reservation(Base):
    """Données persistées pour une réservation."""

    __tablename__ = "reservation"

    id_reservation: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    id_user: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    id_voyage: Mapped[int] = mapped_column(Integer, nullable=False, index=True)
    id_paiement: Mapped[int | None] = mapped_column(Integer, nullable=True)
    date_reservation: Mapped[datetime] = mapped_column(
        DateTime,
        nullable=False,
        default=func.now(),
        server_default=func.now(),
    )
    statut: Mapped[ReservationStatus] = mapped_column(
        Enum(ReservationStatus, name="reservation_status"),
        nullable=False,
        default=ReservationStatus.EN_ATTENTE,
        server_default=ReservationStatus.EN_ATTENTE.value,
    )
    nombre_voyageurs: Mapped[int] = mapped_column(Integer, nullable=False)
    prix_total: Mapped[Decimal] = mapped_column(Numeric(10, 2), nullable=False)

    voyageurs: Mapped[list["Voyageur"]] = relationship(
        back_populates="reservation",
        cascade="all, delete-orphan",
        passive_deletes=True,
    )
    billet: Mapped["Billet | None"] = relationship(
        back_populates="reservation",
        uselist=False,
        cascade="all, delete-orphan",
        passive_deletes=True,
    )
