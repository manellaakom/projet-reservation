"""Routes de la version 1 de l'API."""

from fastapi import APIRouter

from app.api.v1 import disponibilites, reservations, voyageurs

api_router = APIRouter()
api_router.include_router(reservations.router)
api_router.include_router(disponibilites.router)
api_router.include_router(voyageurs.router)
