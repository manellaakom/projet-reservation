# Module 2 — Recherche & Consultation de Voyages et Billets

Microservice **Spring Boot 3.2.5 (Java 17)** permettant de :
1. **Rechercher des voyages et des billets** (vols, trains, bateaux, séjours packagés)
2. **Filtrer et trier les résultats** (destination, départ, dates, budget min/max, compagnie, type de transport, classe de billet, trajet direct, promotions, bagages inclus, annulation gratuite + tri par prix, durée, note, popularité ou score qualité/prix)
3. **Consulter les offres en détail** (itinéraire, services à bord, classes de billets associées, conditions tarifaires, compteur de popularité)
4. **Comparer plusieurs offres côte-à-côte** (calcul automatique du meilleur prix, du trajet le plus court, de la meilleure note, du meilleur score qualité/prix sur 100 et synthèse comparative)
5. **Proposer des suggestions intelligentes** (offres tendances, meilleures promotions, petits budgets, offres similaires et alternatives moins chères)

---

## 🏗️ Architecture & Intégration Microservices

- **Nom Eureka** : `recherche-consultation`
- **Port HTTP** : `8082`
- **Swagger UI** : `http://localhost:8082/swagger-ui.html`
- **Console H2** : `http://localhost:8082/h2-console` (`jdbc:h2:mem:recherche_db`)
- **Profil MySQL** : `--spring.profiles.active=mysql` (`recherche_consultation_db`)
- **Communication Inter-services** :
  - S'enregistre auprès d'**Eureka Discovery Server** (`http://localhost:8761/eureka`)
  - Client OpenFeign `ReservationDisponibiliteClient` connecté au **Module 3 (Gestion de Réservation)**

---

## 🚀 Lancement du Microservice

Depuis `Backend/module2-recherche-consultation` :

```powershell
.\mvnw.cmd spring-boot:run
```

Ou avec le profil MySQL :

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=mysql"
```

---

## 📌 Principaux Endpoints REST (`/api/recherche`)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/recherche/voyages` | Recherche multicritères, filtrage et tri des voyages |
| `POST` | `/api/recherche/voyages/search` | Recherche multicritères via payload JSON |
| `GET` | `/api/recherche/voyages/{id}` | Consultation détaillée d'une offre (+ incrémentation popularité) |
| `POST` | `/api/recherche/voyages` | Création d'une offre de voyage avec ses billets |
| `PUT` | `/api/recherche/voyages/{id}` | Modification d'une offre de voyage |
| `DELETE` | `/api/recherche/voyages/{id}` | Suppression d'une offre de voyage |
| `GET` | `/api/recherche/billets` | Recherche, filtrage et tri direct des offres de billets |
| `GET` | `/api/recherche/billets/{id}` | Consultation détaillée d'un billet |
| `GET` | `/api/recherche/comparer?ids=1,2,3` | Comparaison côte-à-côte de 2 à 5 offres avec synthèse |
| `POST` | `/api/recherche/comparer` | Comparaison côte-à-côte via liste JSON d'IDs |
| `GET` | `/api/recherche/suggestions` | Suggestions globales (tendances, promos, petits budgets, score) |
| `GET` | `/api/recherche/voyages/{id}/suggestions` | Suggestions d'offres similaires et moins chères pour une offre |
| `GET` | `/api/recherche/statistiques` | Statistiques du catalogue et listes dynamiques de filtres |
