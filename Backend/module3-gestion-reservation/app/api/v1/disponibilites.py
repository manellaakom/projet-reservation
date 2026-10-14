"""Routeur réservé aux endpoints des disponibilités."""

from fastapi import APIRouter

router = APIRouter(prefix="/api/v1/disponibilites", tags=["disponibilites"])

# TODO: ajouter les endpoints des disponibilités.
