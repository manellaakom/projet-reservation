"""Énumérations persistées par les modèles du service."""

from enum import Enum


class ReservationStatus(str, Enum):
    """États possibles d'une réservation."""

    EN_ATTENTE = "EN_ATTENTE"
    CONFIRMEE = "CONFIRMEE"
    ANNULEE = "ANNULEE"
    EXPIREE = "EXPIREE"


class DisponibiliteStatus(str, Enum):
    """États possibles de disponibilité d'un voyage."""

    DISPONIBLE = "DISPONIBLE"
    COMPLETE = "COMPLETE"
    INDISPONIBLE = "INDISPONIBLE"
