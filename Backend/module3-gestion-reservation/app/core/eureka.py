"""Enregistrement et découverte des services via Eureka."""

import logging
from typing import Any

from py_eureka_client import eureka_client
from py_eureka_client.http_client import HttpResponse

from app.core.config import settings

logger = logging.getLogger(__name__)
_registered = False


async def register_with_eureka() -> None:
    """Enregistre ce service dans Eureka sans empêcher son démarrage en cas d'échec."""
    global _registered
    if not settings.EUREKA_ENABLED:
        logger.info("Enregistrement Eureka désactivé.")
        return

    base_url = (
        f"http://{settings.EUREKA_INSTANCE_HOST}:{settings.EUREKA_INSTANCE_PORT}"
    )
    client_options: dict[str, Any] = {}
    if settings.EUREKA_USER:
        client_options["eureka_basic_auth_user"] = settings.EUREKA_USER
    if settings.EUREKA_PASSWORD:
        client_options["eureka_basic_auth_password"] = settings.EUREKA_PASSWORD

    try:
        await eureka_client.init_async(
            eureka_server=settings.EUREKA_SERVER_URL,
            app_name=settings.EUREKA_APP_NAME,
            instance_port=settings.EUREKA_INSTANCE_PORT,
            instance_host=settings.EUREKA_INSTANCE_HOST,
            health_check_url=f"{base_url}/health",
            status_page_url=f"{base_url}/health",
            renewal_interval_in_secs=settings.EUREKA_RENEWAL_INTERVAL,
            duration_in_secs=settings.EUREKA_DURATION,
            **client_options,
        )
        _registered = True
        logger.info(
            "Service %s enregistré auprès de Eureka (%s).",
            settings.EUREKA_APP_NAME,
            settings.EUREKA_SERVER_URL,
        )
    except Exception:
        logger.warning(
            "Échec de l'enregistrement de %s auprès de Eureka (%s). "
            "Le service continue sans découverte.",
            settings.EUREKA_APP_NAME,
            settings.EUREKA_SERVER_URL,
            exc_info=True,
        )


async def deregister_from_eureka() -> None:
    """Arrête le client Eureka et désenregistre le service à l'arrêt."""
    global _registered
    if not settings.EUREKA_ENABLED or not _registered:
        return

    try:
        await eureka_client.stop_async()
        logger.info("Service %s désenregistré d'Eureka.", settings.EUREKA_APP_NAME)
    except Exception:
        logger.warning(
            "Échec du désenregistrement de %s auprès d'Eureka.",
            settings.EUREKA_APP_NAME,
            exc_info=True,
        )
    finally:
        _registered = False


async def call_service(app_name: str, path: str) -> str | dict[str, Any] | HttpResponse:
    """Appelle un service découvert dans Eureka par son nom et son chemin."""
    return await eureka_client.do_service_async(app_name, path)
