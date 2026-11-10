"""Modèle SQLAlchemy du billet émis pour une réservation."""

from datetime import datetime

from sqlalchemy import DateTime, ForeignKey, Integer, String, UniqueConstraint, func
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.database import Base


class Billet(Base):
    """Billet associé à une réservation."""

    __tablename__ = "billet"
    __table_args__ = (
        UniqueConstraint("id_reservation", name="uq_billet_id_reservation"),
        UniqueConstraint("code_billet", name="uq_billet_code_billet"),
    )

    id_billet: Mapped[int] = mapped_column(Integer, primary_key=True, autoincrement=True)
    id_reservation: Mapped[int] = mapped_column(
        ForeignKey("reservation.id_reservation"),
        nullable=False,
    )
    code_billet: Mapped[str] = mapped_column(String(50), nullable=False)
    date_emission: Mapped[datetime] = mapped_column(
        DateTime,
        nullable=False,
        server_default=func.now(),
    )

    reservation: Mapped["Reservation"] = relationship(back_populates="billet")
