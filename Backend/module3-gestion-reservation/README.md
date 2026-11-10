# Module 3 — Gestion de réservation

Squelette backend FastAPI pour le microservice de gestion de réservation. Le service possède sa propre base MySQL `reservation_db`. Les identifiants User, Voyage et Paiement restent des entiers sans clés étrangères, car ces ressources sont gérées par d'autres microservices.

## Prérequis

- Python 3.11+
- MySQL 8

## Configuration et lancement local

Depuis `Backend/module3-gestion-reservation` :

```powershell
Copy-Item .env.example .env
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

Configurer les variables `DB_*` du fichier `.env` avec les paramètres d'un serveur MySQL 8 déjà démarré et accessible, puis appliquer les migrations et lancer le service :

```powershell
alembic revision --autogenerate -m "init"
alembic upgrade head
uvicorn app.main:app --reload
```

Vérifications HTTP :

- `GET http://127.0.0.1:8000/health` : disponibilité du processus.
- `GET http://127.0.0.1:8000/health/db` : exécute `SELECT 1` sur MySQL.

## Migrations

La configuration Alembic récupère l'URL de connexion depuis `Settings` et charge `Base.metadata` après import de tous les modèles. Générer une migration après modification des modèles :

```powershell
alembic revision --autogenerate -m "description"
alembic upgrade head
```

Les scripts générés sont suivis dans `migrations/versions/`. L'autogénération requiert que MySQL soit démarré et accessible.

## Périmètre

Les routes HTTP se limitent aux contrôles de santé. Les couches `api`, `schemas`, `repositories` et `services` sont des emplacements préparés avec des TODO ; aucune logique métier ni communication interservices n'est implémentée.
