"""Tests d'integration de l'API (SQLite en memoire, sans MySQL ni Eureka)."""

from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

from app.core.database import Base, get_db
from app.main import app
from app import models  # noqa: F401

engine = create_engine(
    "sqlite://", connect_args={"check_same_thread": False}, poolclass=StaticPool
)
TestingSession = sessionmaker(bind=engine, autocommit=False, autoflush=False)


def override_get_db():
    db = TestingSession()
    try:
        yield db
    finally:
        db.close()


app.dependency_overrides[get_db] = override_get_db
client = TestClient(app)  # sans "with" : le lifespan (MySQL/Eureka) n'est pas execute


def setup_function():
    Base.metadata.drop_all(engine)
    Base.metadata.create_all(engine)


def _voyageur(nom="Ben Ali"):
    return {
        "nom": nom,
        "prenom": "Sami",
        "date_naissance": "1995-04-12",
        "numero_piece_identite": "AB123456",
    }


def test_health():
    assert client.get("/health").json() == {"status": "ok"}


def test_disponibilite_crud():
    r = client.post("/api/v1/disponibilites", json={"id_voyage": 1, "nombre_places": 10})
    assert r.status_code == 201
    assert client.post("/api/v1/disponibilites", json={"id_voyage": 1, "nombre_places": 5}).status_code == 409
    r = client.get("/api/v1/disponibilites/voyage/1")
    assert r.status_code == 200 and r.json()["places_disponibles"] == 10
    assert client.get("/api/v1/disponibilites/voyage/99").status_code == 404
    r = client.put("/api/v1/disponibilites/voyage/1", json={"nombre_places": 20})
    assert r.status_code == 200 and r.json()["nombre_places"] == 20


def test_reservation_lifecycle():
    client.post("/api/v1/disponibilites", json={"id_voyage": 1, "nombre_places": 3})
    payload = {
        "id_user": 1, "id_voyage": 1, "nombre_voyageurs": 2, "prix_total": "500.00",
        "voyageurs": [_voyageur("A"), _voyageur("B")],
    }
    r = client.post("/api/v1/reservations", json=payload)
    assert r.status_code == 201, r.text
    body = r.json()
    assert body["statut"] == "EN_ATTENTE"
    assert body["codeReservation"] == f"RES-{body['id_reservation']:06d}"
    assert body["valide"] is True and body["voyageurNom"] == "Sami A"
    assert client.get("/api/v1/disponibilites/voyage/1").json()["places_disponibles"] == 1

    # pas assez de places
    assert client.post("/api/v1/reservations", json=payload).status_code == 409

    # lecture par id numerique et par code (contrat Feign du Module 1)
    rid = body["id_reservation"]
    assert client.get(f"/api/v1/reservations/{rid}").status_code == 200
    assert client.get(f"/api/v1/reservations/{body['codeReservation']}").status_code == 200
    assert client.get("/api/v1/reservations/999").status_code == 404
    assert len(client.get(f"/api/v1/voyageurs/reservation/{rid}").json()) == 2

    # confirmation -> billet
    r = client.put(f"/api/v1/reservations/{rid}/confirmer", json={"id_paiement": 7})
    assert r.status_code == 200
    assert r.json()["statut"] == "CONFIRMEE" and r.json()["billet"]["code_billet"].startswith("BIL-")
    assert client.put(f"/api/v1/reservations/{rid}/confirmer", json={"id_paiement": 7}).status_code == 409

    # annulation -> places restituees
    r = client.patch(f"/api/v1/reservations/{rid}/annuler")
    assert r.status_code == 200 and r.json()["statut"] == "ANNULEE" and r.json()["valide"] is False
    assert client.get("/api/v1/disponibilites/voyage/1").json()["places_disponibles"] == 3
    assert client.patch(f"/api/v1/reservations/{rid}/annuler").status_code == 409


def test_reservation_validations():
    assert client.post("/api/v1/reservations", json={
        "id_user": 1, "id_voyage": 5, "nombre_voyageurs": 1, "prix_total": "10"}).status_code == 404
    client.post("/api/v1/disponibilites", json={"id_voyage": 5, "nombre_places": 5})
    assert client.post("/api/v1/reservations", json={
        "id_user": 1, "id_voyage": 5, "nombre_voyageurs": 3, "prix_total": "10",
        "voyageurs": [_voyageur()]}).status_code == 422
