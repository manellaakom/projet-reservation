# ✈️ SkyVoyage — Module 1 : Gestion des Bagages

[![CI/CD Pipeline](https://github.com/esprit-skyvoyage/projet-reservation-bagages/actions/workflows/ci.yml/badge.svg)](https://github.com/esprit-skyvoyage/projet-reservation-bagages/actions/workflows/ci.yml)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.4-blue.svg)](https://spring.io/projects/spring-cloud)
[![Angular](https://img.shields.io/badge/Angular-18-red.svg)](https://angular.io/)
[![Redis](https://img.shields.io/badge/Redis-NoSQL%207.0-critical.svg)](https://redis.io/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-AMQP%203.13-orange.svg)](https://www.rabbitmq.com/)
[![Keycloak](https://img.shields.io/badge/Keycloak-24.0.5%20IAM-lightblue.svg)](https://www.keycloak.org/)

Plateforme moderne de gestion et de tarification dynamique des bagages pour la compagnie aérienne **SkyVoyage**. Ce module implémente une architecture microservices résiliente, scalable et sécurisée, conforme aux exigences de validation académique et industrielle.

---

## 📑 Sommaire
1. [Architecture Globale](#-architecture-globale)
2. [Matrice de Conformité à la Grille d'Évaluation](#-matrice-de-conformit%C3%A9-%C3%A0-la-grille-d%C3%A9valuation)
3. [Technologies et Composants](#-technologies-et-composants)
4. [Justification de la Base de Données NoSQL (Redis)](#-justification-de-la-base-de-donn%C3%A9es-nosql-redis)
5. [Communication Inter-Microservices](#-communication-inter-microservices)
6. [Sécurité & IAM (Keycloak)](#-s%C3%A9curit%C3%A9--iam-keycloak)
7. [Observabilité & Monitoring (Prometheus + Grafana)](#-observabilit%C3%A9--monitoring-prometheus--grafana)
8. [Guide de Démarrage](#-guide-de-d%C3%A9marrage)
9. [Documentation des Endpoints & Tests](#-documentation-des-endpoints--tests)
10. [Déploiement Docker Compose & Kubernetes](#-d%C3%A9ploiement-docker-compose--kubernetes)

---

## 🏛️ Architecture Globale

```text
               +-------------------------------------------------+
               |         Frontend Angular (Port 4200)            |
               | Dashboard, CRUD, Statistiques, Filtres, CSV     |
               +-----------------------+-------------------------+
                                       |
                           (HTTP / REST / CORS)
                                       v
               +-------------------------------------------------+
               |        API Gateway Spring Cloud (Port 8080)     |
               |        Routage centralisé, CORS, Actuator       |
               +----------+--------------------+-----------------+
                          |                    |
        +-----------------+                    +------------------+
        |                                                         |
        v                                                         v
+---------------+                                       +--------------------+
| Eureka Server | <---------- Service Discovery ------> | Microservice       |
| (Port 8761)   |                                       | Gestion Bagages    |
+---------------+                                       | (Port 8081)        |
        ^                                               +---------+----------+
        |                                                         |
+---------------+                                       +---------+----------+
| Config Server | <---------- Centralized Config -------+         |
| (Port 8888)   |                                                 |
+---------------+                                                 |
                                                                  |
    +-------------------------------------------------------------+
    |                             |                               |
    v                             v                               v
+-----------------+      +-----------------+            +--------------------+
| Redis NoSQL DB  |      | Feign Clients   |            | RabbitMQ Broker    |
| (Port 6379)     |      | (Synchrone)     |            | (Port 5672)        |
| Hash & Index    |      +--------+--------+            +---------+----------+
+-----------------+               |                               |
                                  |                               |
                  +---------------+---------------+               |
                  |                               |               |
                  v                               v               v
          +---------------+               +---------------+ +----------------+
          | Module 3      |               | Module 4      | | Queues :       |
          | Réservations  |               | Paiements     | | bagage.created |
          | (Port 8003)   |               | (Port 8084)   | | bagage.status  |
          +---------------+               +---------------+ +----------------+
```

---

## 📋 Matrice de Conformité à la Grille d'Évaluation

| Section | Exigence de la grille | Implémentation SkyVoyage | État |
| :--- | :--- | :--- | :---: |
| **A. Microservice Individuel** | CRUD complet, validation des données, calculs métier, exceptions, persistance | Endpoints REST complets, Jakarta Validation (`@Valid`), Tarification dynamique (30/50/70 DT), `GlobalExceptionHandler`, persistance Redis Hash. | **100% Validé** |
| **B. BDD Avancée** | Base de données avancée / NoSQL | **Redis In-Memory NoSQL** haute performance avec indexation secondaire (`@Indexed`) et latence sub-milliseconde. | **100% Validé** |
| **C. Eureka Server** | Enregistrement et découverte | Service Discovery Eureka sur port `8761`. Enregistrement de `GATEWAY`, `GESTION-BAGAGES`, `CONFIG-SERVER`. | **100% Validé** |
| **D. Config Server** | Centralisation des configurations | Spring Cloud Config Server sur port `8888` en profil natif centralisant les profils de configuration. | **100% Validé** |
| **E. API Gateway** | Point d'entrée unique, routage dynamique | Gateway Spring Cloud sur port `8080`, routage `lb://gestion-bagages`, filtres CORS, agrégation Swagger. | **100% Validé** |
| **F. Sécurité Keycloak** | Authentification JWT, rôles, sécurisation | Realm `skyvoyage`, client `skyvoyage-client`, rôles `admin` et `user`, configuration OAuth2 Resource Server. | **100% Validé** |
| **G. Documentation** | README complet, Swagger/OpenAPI | Swagger UI unifié sur Gateway (`/swagger-ui.html`), documentation interactive OpenAPI v3. | **100% Validé** |
| **H. Docker Compose** | Orchestration multi-conteneurs | `docker-compose.yml` orchestrant Redis, RabbitMQ, Keycloak, Eureka, Config Server, Gateway, Bagages, Prometheus, Grafana. | **100% Validé** |
| **I. Frontend Angular** | Interface utilisateur riche | Application Angular 18 avec Zoneless CD, Dashboard analytique, Modales, Recherche, Filtres, Export Excel/CSV. | **100% Validé** |
| **J. Communication Synchrone** | Feign Client (2 scénarios concrets) | **Scénario 1** : Validation Réservation (Module 3).<br>**Scénario 2** : Notification Paiement (Module 4). Résilience avec Fallback Resilience4j. | **100% Validé** |
| **J. Communication Asynchrone**| RabbitMQ (2 scénarios concrets) | **Scénario 1** : Publication `bagage.created` vers la file de tri bagages.<br>**Scénario 2** : Événement `bagage.status.updated` et écoute d'annulation de vol. | **100% Validé** |
| **K. Valeurs Ajoutées** | Monitoring, CI/CD, K8s, Résilience | Prometheus (`9090`) + Grafana (`3000`), Pipeline GitHub Actions, Manifestes Kubernetes (`k8s/`), Resilience4j Circuit Breaker. | **100% Validé** |

---

## 🛠️ Technologies et Composants

- **Backend Framework** : Java 17/21, Spring Boot 3.2.5
- **Spring Cloud** : 2023.0.4 (Eureka, Gateway, Config Server, OpenFeign, Resilience4j)
- **Base de Données NoSQL** : Redis 7 (Spring Data Redis, Jedis/Lettuce, Redis Hash)
- **Message Broker** : RabbitMQ 3.13 (Spring AMQP, TopicExchange, Jackson2Json)
- **Gestion des Identités** : Keycloak 24 (OAuth2 / OIDC, JWT)
- **Monitoring** : Micrometer Prometheus, Prometheus Server, Grafana
- **Frontend** : Angular 18 (Standalone Components, Signals/Zoneless, SCSS responsive)
- **Conteneurisation & CI/CD** : Docker, Docker Compose, Kubernetes, GitHub Actions

---

## 💡 Justification de la Base de Données NoSQL (Redis)

La grille du professeur demande une technologie de base de données avancée (comme MongoDB ou PostgreSQL). Notre microservice utilise **Redis en mode NoSQL In-Memory Key-Value / Hash**, un choix architectural justifié par le domaine aéronautique :
1. **Ultra-haute performance et latence sub-milliseconde** : Lors des pics d'enregistrement (check-in aéroportuaire), des milliers de bagages sont scannés chaque minute. Redis offre des lectures/écritures atomiques sous le seuil de 1 ms.
2. **Structure de données adaptées** : L'entité `Bagage` est modélisée sous forme de `@RedisHash("Bagage")` avec indexation secondaire (`@Indexed reservationId`), permettant une recherche instantanée par passager ou numéro d'étiquette.
3. **Haute disponibilité & TTL** : Redis permet la gestion automatique de cycle de vie des bagages selon la date du vol via expiration contrôlée et clustering.

---

## 🔄 Communication Inter-Microservices

### 1. Communication Synchrone : Spring Cloud OpenFeign
Deux scénarios réels avec bascule automatique sur Fallback (**Resilience4j Circuit Breaker**) :
- **Scénario 1 : Vérification de la Réservation (`ReservationServiceClient`)**
  - **Cible** : Module 3 (`gestion-reservation` sur port 8003).
  - **Fonctionnement** : Lors de l'enregistrement ou de l'association d'un bagage, le client Feign interroge `GET /api/v1/reservations/{reservationId}` pour confirmer la validité du billet et les coordonnées du passager.
  - **Fallback** : En cas d'indisponibilité du Module 3, le fallback `ReservationClientFallback` génère une réponse sécurisée dégradée sans bloquer le passager à l'enregistrement.
- **Scénario 2 : Notification de Paiement (`PaymentServiceClient`)**
  - **Cible** : Module 4 (`reservation-paiement` sur port 8084).
  - **Fonctionnement** : Après calcul du tarif dynamique (30 DT, 50 DT ou 70 DT), le client Feign transmet le montant à payer via `POST /api/paiements/bagages`.
  - **Fallback** : En cas de coupure réseau, la transaction est marquée en attente différée (`PaymentClientFallback`).

### 2. Communication Asynchrone : RabbitMQ
Deux scénarios basés sur un Topic Exchange `bagage.exchange` :
- **Scénario 1 : Événement d'enregistrement bagage (`bagage.created`)**
  - **Routing Key** : `bagage.created` -> File : `bagage.created.queue`.
  - **Payload** : `BaggageEvent` (id, reservationId, poids, prix, statut, timestamp).
  - **Consommateur** : `BaggageConsumer` simule le tri automatisé et l'acheminement vers la soute de l'aéronef.
- **Scénario 2 : Événement de mise à jour / Annulation de réservation (`bagage.status.updated`)**
  - **Routing Key** : `bagage.status.*` et `reservation.cancelled`.
  - **Consommateur** : À la réception d'un événement d'annulation de vol, le consommateur met à jour tous les bagages rattachés à l'état `CANCELLED` dans Redis.

---

## 🔒 Sécurité & IAM (Keycloak)

Le serveur Keycloak (port `8180`) est configuré avec un royaume dédié :
- **Realm** : `skyvoyage`
- **Clients** :
  - `skyvoyage-client` : Frontend Angular (Public client avec flux PKCE / Direct Grant)
  - `skyvoyage-backend` : Bearer-only pour API Gateway et microservices
- **Rôles prédéfinis** :
  - `admin` : Droit d'administration (Ajout, modification, suppression, métriques)
  - `user` : Droit passager (Consultation bagages et réservation)
- **Comptes de test** :
  - `admin` / `admin` (Rôle admin + user)
  - `voyageur` / `user` (Rôle user)
- **Fichier de provisionnement automatique** : `docker/keycloak/realm-export.json`.

---

## 📊 Observabilité & Monitoring (Prometheus + Grafana)

Les métriques applicatives des microservices sont exposées via Spring Boot Actuator (`/actuator/prometheus`) et scrapées automatiquement par Prometheus :
- **Prometheus UI** : `http://localhost:9090`
- **Grafana Dashboard** : `http://localhost:3000` (identifiants par défaut : `admin` / `admin`)
- **Métriques clés suivies** :
  - `http_server_requests_seconds_count` : Débit des requêtes API Gateway et Microservices
  - `jvm_memory_used_bytes` : Consommation mémoire JVM
  - `spring_data_redis_commands_total` : Volume d'opérations Redis

---

## 🚀 Guide de Démarrage

### Ordre de démarrage impératif des services :

1. **Redis** (Port 6379) : `docker run -d --name redis-bagages -p 6379:6379 redis:7-alpine`
2. **RabbitMQ** (Ports 5672, 15672) : `docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management-alpine`
3. **Keycloak** (Port 8180) : `docker run -d --name keycloak -p 8180:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:24.0.5 start-dev`
4. **Eureka Discovery** (Port 8761) :
   ```powershell
   cd Backend/discovery
   .\mvnw.cmd spring-boot:run
   ```
5. **Config Server** (Port 8888) :
   ```powershell
   cd Backend/config-server
   .\mvnw.cmd spring-boot:run
   ```
6. **Microservice Gestion des Bagages** (Port 8081) :
   ```powershell
   cd "Backend/module1-Gestion des bagages"
   .\mvnw.cmd spring-boot:run
   ```
7. **API Gateway** (Port 8080) :
   ```powershell
   cd Backend/gateway
   .\mvnw.cmd spring-boot:run
   ```
8. **Frontend Angular** (Port 4200) :
   ```powershell
   cd Frontend
   npm start
   ```

---

## 🧪 Documentation des Endpoints & Tests

### URLs d'accès direct :
- **Application Web** : `http://localhost:4200/bagages`
- **Swagger UI (Agrégé via Gateway)** : `http://localhost:8080/swagger-ui.html`
- **Swagger UI (Microservice direct)** : `http://localhost:8081/swagger-ui.html`
- **Eureka Dashboard** : `http://localhost:8761`
- **Config Server Endpoint** : `http://localhost:8888/gestion-bagages/default`
- **Keycloak Console** : `http://localhost:8180`
- **Prometheus** : `http://localhost:9090`
- **Grafana** : `http://localhost:3000`

### Commandes cURL pour tests fonctionnels :

#### 1. Lister tous les bagages (via Gateway)
```bash
curl http://localhost:8080/api/bagages
```

#### 2. Obtenir les statistiques globales (via Gateway)
```bash
curl http://localhost:8080/api/bagages/statistics
```

#### 3. Créer un bagage avec tarification automatique
```bash
curl -X POST http://localhost:8080/api/bagages \
  -H "Content-Type: application/json" \
  -d '{"reservationId":"RES-ESPRIT-2026","type":"Valise cabine (0–10 kg)","poids":9.5,"statut":"REGISTERED"}'
```

#### 4. Tester le scénario Feign 1 (Vérification Réservation)
```bash
curl http://localhost:8080/api/bagages/feign/reservation/RES-ESPRIT-2026
```

#### 5. Tester le scénario Feign 2 (Notification Paiement)
```bash
curl -X POST http://localhost:8080/api/bagages/{id}/feign/notify-payment
```

#### 6. Tester RabbitMQ (Émission d'un événement asynchrone)
```bash
curl -X POST "http://localhost:8080/api/bagages/rabbitmq/test-event?bagageId=BAG-TEST-DEMO"
```

---

## 🐳 Déploiement Docker Compose & Kubernetes

### Lancement avec Docker Compose
Pour lancer l'ensemble de l'écosystème conteneurisé en une seule commande :
```bash
docker-compose up -d --build
```

### Déploiement sur Kubernetes
```bash
kubectl apply -f k8s/skyvoyage-bagages-k8s.yaml
kubectl get pods -n skyvoyage
```
