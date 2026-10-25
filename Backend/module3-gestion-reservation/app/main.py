"""Point d'entrée FastAPI du service de réservation."""

import logging
from collections.abc import AsyncIterator
from contextlib import asynccontextmanager

from fastapi import FastAPI
from sqlalchemy import text

from app.core.config import settings
from app.core.database import (
    Base,
    SessionLocal,
    create_database_if_not_exists,
    init_db,
)
from app.core.eureka import deregister_from_eureka, register_with_eureka
from app.models import Billet, Disponibilite, Reservation, Voyageur  # noqa: F401

logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(_: FastAPI) -> AsyncIterator[None]:
    """Initialise la base puis gère l'enregistrement Eureka."""
    if settings.DB_AUTO_CREATE:
        create_database_if_not_exists()
        init_db()
        logger.info("Base créée/vérifiée : %s", settings.DB_NAME)
        logger.info(
            "Tables créées/vérifiées : %s",
            sorted(Base.metadata.tables.keys()),
        )
    if settings.EUREKA_ENABLED:
        await register_with_eureka()
    try:
        yield
    finally:
        if settings.EUREKA_ENABLED:
            await deregister_from_eureka()


app = FastAPI(title=settings.APP_NAME, debug=settings.DEBUG, lifespan=lifespan)


@app.get("/health")
def health() -> dict[str, str]:
    """Vérifie que le processus HTTP est disponible."""
    return {"status": "ok"}


@app.get("/health/db")
def health_db() -> dict[str, str]:
    """Vérifie la connexion au serveur MySQL avec une requête minimale."""
    with SessionLocal() as db:
        db.execute(text("SELECT 1"))
    return {"status": "ok", "database": "connected"}
