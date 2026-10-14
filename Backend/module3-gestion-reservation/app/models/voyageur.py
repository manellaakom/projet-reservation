"""Modèle SQLAlchemy d'un voyageur associé à une réservation."""

from datetime import date

from sqlalchemy import Date, ForeignKey, Integer, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.database import Base


class Voyageur(Base):
    """Données d'identité d'un voyageur."""

    __tablename__ = "voyageur"

    id_voyageur: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    id_reservation: Mapped[int] = mapped_column(
        ForeignKey("reservation.id_reservation", ondelete="CASCADE"),
        nullable=False,
    )
    nom: Mapped[str] = mapped_column(String(100), nullable=False)
    prenom: Mapped[str] = mapped_column(String(100), nullable=False)
    date_naissance: Mapped[date] = mapped_column(Date, nullable=False)
    numero_piece_identite: Mapped[str] = mapped_column(String(100), nullable=False)

    reservation: Mapped["Reservation"] = relationship(back_populates="voyageurs")
