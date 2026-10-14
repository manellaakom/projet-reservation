"""Configuration de l'application chargée depuis l'environnement ou `.env`."""

from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict
from sqlalchemy import URL


class Settings(BaseSettings):
    """Paramètres applicatifs et de connexion à MySQL."""

    DB_HOST: str = "localhost"
    DB_PORT: int = 3306
    DB_USER: str = "root"
    DB_PASSWORD: str = ""
    DB_NAME: str = "reservation_db"
    DB_ECHO: bool = False
    DB_AUTO_CREATE: bool = True
    DB_RESET_ON_STARTUP: bool = False
    APP_NAME: str = "reservation-service"
    DEBUG: bool = False
    EUREKA_ENABLED: bool = True
    EUREKA_SERVER_URL: str = "http://localhost:8761/eureka/"
    EUREKA_APP_NAME: str = "RESERVATION-SERVICE"
    EUREKA_INSTANCE_HOST: str = "localhost"
    EUREKA_INSTANCE_PORT: int = 8003
    EUREKA_RENEWAL_INTERVAL: int = 30
    EUREKA_DURATION: int = 90
    EUREKA_USER: str | None = None
    EUREKA_PASSWORD: str | None = None

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        extra="ignore",
    )

    @property
    def database_url(self) -> URL:
        """Construit une URL SQLAlchemy en échappant correctement les identifiants."""
        return URL.create(
            drivername="mysql+pymysql",
            username=self.DB_USER,
            password=self.DB_PASSWORD,
            host=self.DB_HOST,
            port=self.DB_PORT,
            database=self.DB_NAME,
        )


@lru_cache
def get_settings() -> Settings:
    """Retourne l'instance de configuration mise en cache."""
    return Settings()


settings = get_settings()
