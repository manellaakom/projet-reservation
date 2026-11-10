"""Modèle SQLAlchemy des places disponibles pour un voyage."""

from sqlalchemy import Enum, Integer, UniqueConstraint
from sqlalchemy.orm import Mapped, mapped_column

from app.core.database import Base
from app.models.enums import DisponibiliteStatus


class Disponibilite(Base):
    """Nombre de places et état de disponibilité d'un voyage."""

    __tablename__ = "disponibilite"
    __table_args__ = (UniqueConstraint("id_voyage", name="uq_disponibilite_id_voyage"),)

    id_disponibilite: Mapped[int] = mapped_column(
        Integer,
        primary_key=True,
        autoincrement=True,
    )
    id_voyage: Mapped[int] = mapped_column(Integer, nullable=False)
    nombre_places: Mapped[int] = mapped_column(Integer, nullable=False)
    places_disponibles: Mapped[int] = mapped_column(Integer, nullable=False)
    statut: Mapped[DisponibiliteStatus] = mapped_column(
        Enum(DisponibiliteStatus, name="disponibilite_status"),
        nullable=False,
        default=DisponibiliteStatus.DISPONIBLE,
        server_default=DisponibiliteStatus.DISPONIBLE.value,
    )
