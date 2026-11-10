"""Moteur SQLAlchemy, sessions et dépendance de base de données."""

from collections.abc import Generator
import logging

from sqlalchemy import URL, create_engine, text
from sqlalchemy.exc import OperationalError
from sqlalchemy.orm import DeclarativeBase, Session, sessionmaker

from app.core.config import settings

logger = logging.getLogger(__name__)


class Base(DeclarativeBase):
    """Classe de base des modèles SQLAlchemy."""


database_url = URL.create(
    "mysql+pymysql",
    username=settings.DB_USER,
    password=settings.DB_PASSWORD,
    host=settings.DB_HOST,
    port=settings.DB_PORT,
    database=settings.DB_NAME,
    query={"charset": "utf8mb4"},
)

engine = create_engine(
    database_url,
    pool_pre_ping=True,
    echo=settings.DB_ECHO,
)
SessionLocal = sessionmaker(
    bind=engine,
    autocommit=False,
    autoflush=False,
)


def create_database_if_not_exists() -> None:
    """Crée la base configurée en se connectant au serveur sans sélectionner de base."""
    admin_url = URL.create(
        "mysql+pymysql",
        username=settings.DB_USER,
        password=settings.DB_PASSWORD,
        host=settings.DB_HOST,
        port=settings.DB_PORT,
        query={"charset": "utf8mb4"},
    )
    admin_engine = create_engine(
        admin_url,
        pool_pre_ping=True,
        isolation_level="AUTOCOMMIT",
        echo=settings.DB_ECHO,
    )
    try:
        quoted_database_name = admin_engine.dialect.identifier_preparer.quote_identifier(
            settings.DB_NAME
        )
        with admin_engine.connect() as connection:
            connection.execute(
                text(
                    f"CREATE DATABASE IF NOT EXISTS {quoted_database_name} "
                    "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"
                )
            )
        logger.info("Base créée/vérifiée : %s", settings.DB_NAME)
    except OperationalError:
        logger.exception(
            "MySQL injoignable : démarre MySQL dans XAMPP/WAMP et vérifie "
            "DB_HOST / DB_PORT / DB_USER / DB_PASSWORD"
        )
        raise
    finally:
        admin_engine.dispose()


def init_db() -> None:
    """Importe les modèles puis crée les tables configurées."""
    from app import models  # noqa: F401 - enregistre tous les modèles dans Base.metadata

    try:
        if settings.DB_RESET_ON_STARTUP:
            logger.warning(
                "DB_RESET_ON_STARTUP=true : suppression de toutes les tables et de "
                "leurs données avant leur recréation (développement uniquement)."
            )
            Base.metadata.drop_all(bind=engine)
        Base.metadata.create_all(bind=engine)
        logger.info(
            "Tables créées/vérifiées : %s",
            sorted(Base.metadata.tables.keys()),
        )
    except OperationalError:
        logger.exception(
            "MySQL injoignable : démarre MySQL dans XAMPP/WAMP et vérifie "
            "DB_HOST / DB_PORT / DB_USER / DB_PASSWORD"
        )
        raise


def get_db() -> Generator[Session, None, None]:
    """Fournit une session SQLAlchemy et la ferme après la requête."""
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
