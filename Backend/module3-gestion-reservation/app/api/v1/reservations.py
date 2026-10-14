"""Routeur réservé aux endpoints des réservations."""

from fastapi import APIRouter

router = APIRouter(prefix="/api/v1/reservations", tags=["reservations"])

# TODO: ajouter les endpoints des réservations.
