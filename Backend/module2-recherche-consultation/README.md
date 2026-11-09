# Module 2 — Recherche & Consultation de Voyages et Billets

Microservice **Spring Boot 3.2.5 (Java 17)** — Responsable de la gestion unique de l'entité `Voyage` au sein de la plateforme distribuée.

---

## 🎯 Fonctionnalités Métier

1. **CRUD Complet de l'Entité Voyage** :
   - Création (`POST /api/recherche/voyages`), Lecture (`GET`), Modification (`PUT`), Suppression (`DELETE`).
2. **Recherche Multicritères & Filtrage Dynamique** :
   - Filtres combinables : mot-clé, ville/pays de départ et de destination, dates (min/max), budget min/max, compagnie, type de transport (`VOL`, `TRAIN`, `BATEAU`, `SEJOUR_PACKAGE`), trajet direct (0 escale), promotions, bagage en soute inclus, annulation gratuite, nombre de places minimum, note moyenne minimum.
   - Tri dynamique : par prix (`prix`), durée (`duree`), note (`note`), popularité (`popularite`) en ordre croissant (`asc`) ou décroissant (`desc`).
3. **Consultation Détaillée & Compteur de Popularité** :
   - `GET /api/recherche/voyages/{id}` incrémente automatiquement le compteur de consultations.
4. **Comparateur Intelligent d'Offres (2 à 5 voyages)** :
   - `GET /api/recherche/comparer?ids=1,2,3`
   - Détection automatique du meilleur prix, du trajet le plus court, de la meilleure note.
   - Calcul d'un **score qualité/prix sur 100** avec synthèse textuelle de recommandation.
5. **Moteur de Suggestions Intelligentes** :
   - `GET /api/recherche/suggestions` : tendances, meilleures promotions, petits budgets.
   - `GET /api/recherche/voyages/{id}/suggestions` : offres similaires et alternatives moins chères.
6. **Statistiques Globales du Catalogue** :
   - `GET /api/recherche/statistiques` : nombre d'offres, prix moyen/min/max, villes desservies, compagnies actives.

---

## 🔒 Sécurité Spring Security & JWT (Note 2 de la Grille AWD)

Le microservice intègre une couche de sécurité complète avec **Spring Security 6** et **JJWT (HMAC-SHA256)** :

- **Règles d'accès (RBAC)** :
  - **Consultation publique (sans token)** : Recherche, détails, comparateur, suggestions, statistiques, Swagger UI et Actuator restent accessibles librement pour les voyageurs.
  - **Opérations d'écriture protégées (Token JWT requis)** : `POST /api/recherche/voyages` (Création) et `PUT /api/recherche/voyages/**` (Modification).
  - **Opérations critiques réservées (Rôle `ROLE_ADMIN` requis)** : `DELETE /api/recherche/voyages/**`.
- **Composants Sécurité** :
  - [`SecurityConfig.java`](src/main/java/com/travel/recherche/security/SecurityConfig.java) : Configuration de la chaîne de filtres stateless et des autorisations d'URL.
  - [`JwtUtils.java`](src/main/java/com/travel/recherche/security/JwtUtils.java) : Signature et vérification des tokens JWT.
  - [`JwtAuthenticationFilter.java`](src/main/java/com/travel/recherche/security/JwtAuthenticationFilter.java) : Filtre validant le header `Authorization: Bearer <token>`.
  - [`AuthController.java`](src/main/java/com/travel/recherche/controller/AuthController.java) : Endpoints `/api/recherche/auth/login`, `/register`, `/verify`.
- **Démonstration directe dans Swagger** :
  - `GET /api/recherche/auth/demo-tokens` fournit instantanément un token `ADMIN` et `USER`.
  - Cliquez sur le bouton vert **"Authorize" 🔓** en haut de Swagger UI pour appliquer le token !

---

## 🔄 Communications Inter-Microservices (Note 4 de la Grille AWD)

Toutes les interfaces et implémentations de communication de votre module se trouvent dans :
- Clients Feign : `src/main/java/com/travel/recherche/client/`
- Événements RabbitMQ : `src/main/java/com/travel/recherche/event/`
- Contrôleur de test : `src/main/java/com/travel/recherche/controller/InterServiceCommunicationController.java`

### A. Communications Synchrones (OpenFeign)
| Client OpenFeign | Service Cible | Méthode & Rôle |
|---|---|---|
| [`ReservationDisponibiliteClient.java`](src/main/java/com/travel/recherche/client/ReservationDisponibiliteClient.java) | Module 3 (FastAPI :8003) | `getDisponibiliteByVoyage(id)` : Vérification des disponibilités en temps réel |
| [`BagageClient.java`](src/main/java/com/travel/recherche/client/BagageClient.java) | Module 1 (Bagages :8081) | `getBagageStatistics()`, `getAllBagages()` : Récupération des options bagages |
| [`PaiementClient.java`](src/main/java/com/travel/recherche/client/PaiementClient.java) | Module 4 (Paiement :8084) | `getAllPaiements()` : Audit et consultation des paiements associés |

> **Résilience active** : Chaque appel Feign est encapsulé dans [`InterServiceCommunicationService.java`](src/main/java/com/travel/recherche/service/InterServiceCommunicationService.java) avec un mécanisme de **Fallback automatique** qui évite tout plantage si un service tiers est temporairement hors ligne.

### B. Communications Asynchrones (RabbitMQ)
- **Exchange Direct** : `travel.direct.exchange`
- **Exchange Topic** : `travel.topic.exchange`

| Scénario Asynchrone | Routing Key | Queue | Action Métier dans Module 2 |
|---|---|---|---|
| **1. Réservation créée** | `reservation.created.key` | `recherche.reservation.queue` | Décrémente les places disponibles et booste la popularité (+10) |
| **2. Paiement validé** | `paiement.success.key` | `recherche.paiement.queue` | Confirme la transaction et booste la popularité (+5) |
| **3. Promotion Flash** | `voyage.promo.flash` | `recherche.promotions.queue` | Active le flag promotion et applique le nouveau tarif instantanément |

---

## 🐳 Conteneurisation Docker (Note 2 pts)

### 1. Construire l'image Docker du Module 2 :
```bash
docker build -t module2-recherche:latest .
```

### 2. Lancer Module 2 + MySQL + RabbitMQ avec Docker Compose :
```bash
docker compose up -d
```

Pour arrêter les conteneurs :
```bash
docker compose down
```

---

## 🚀 Lancement en Local (Windows / Maven)

```powershell
.\mvnw.cmd spring-boot:run
```

Ou compilation et packaging du JAR :
```powershell
.\mvnw.cmd clean package -DskipTests
```

Exécution directe du JAR :
```powershell
java -jar target/module2-recherche-consultation-0.0.1-SNAPSHOT.jar
```

---

## 🌐 URLs & Endpoints Clés

- **Swagger UI avec Sécurité** : `http://localhost:8082/swagger-ui.html`
- **Tokens de test JWT** : `http://localhost:8082/api/recherche/auth/demo-tokens`
- **Actuator Health & Metrics** : `http://localhost:8082/actuator/health`
- **Démonstrations Feign** : `http://localhost:8082/api/recherche/inter-services/feign/...`
- **Démonstrations RabbitMQ** : `http://localhost:8082/api/recherche/inter-services/rabbitmq/...`
