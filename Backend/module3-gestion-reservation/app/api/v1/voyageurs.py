"""Routeur réservé aux endpoints des voyageurs."""

from fastapi import APIRouter

router = APIRouter(prefix="/api/v1/voyageurs", tags=["voyageurs"])

# TODO: ajouter les endpoints des voyageurs.
