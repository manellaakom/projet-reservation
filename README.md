# SkyVoyage — Plateforme de Réservation de Voyages & Microservices Distribués

[![CI/CD Pipeline](https://github.com/manellaakom/projet-reservation/actions/workflows/ci.yml/badge.svg)](https://github.com/manellaakom/projet-reservation/actions)
[![Angular](https://img.shields.io/badge/Frontend-Angular%2019-DD0031.svg?logo=angular)](http://localhost:4200)
[![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot%203-6DB33F.svg?logo=springboot)](http://localhost:8080)
[![Keycloak](https://img.shields.io/badge/Security-Keycloak%20OIDC-008080.svg?logo=keycloak)](http://localhost:8180)
[![Docker](https://img.shields.io/badge/Container-Docker%20Compose-2496ED.svg?logo=docker)](https://www.docker.com/)
SkyVoyage est une plateforme cloud-native modulaire basée sur une architecture microservices distribuée.

---

## 🏛️ Architecture Globale du Système

```
                                  +------------------------------------+
                                  |         UTILISATEUR / NAVIGATEUR    |
                                  |   Frontend Angular (Port 4200)     |
                                  +-----------------+------------------+
                                                    | (Auth OIDC & Requêtes HTTP)
                                                    v
+---------------------------------------------------------------------------------------------------+
|                                  API GATEWAY (Port 8080)                                          |
|                Routage dynamique, Filtres CORS, Déduplication d'en-têtes, Swagger Docs             |
+---------+--------------------+---------------------+--------------------+-------------------------+
          |                    |                     |                    |
          v                    v                     v                    v
+--------------------+ +--------------------+ +--------------------+ +--------------------+
|  MODULE 1 BAGAGES  | | MODULE 2 RECHERCHE | | MODULE 3 RESERV.   | |  MODULE 4 PAIEMENT |
|   (Spring Boot)    | |   (Spring Boot)    | | (Python / FastAPI) | |   (Spring Boot)    |
|     Port 8081      | |     Port 8082      | |     Port 8003      | |     Port 8084      |
|    Redis NoSQL     | |    MySQL 3307      | |     PostgreSQL     | |      H2 / JPA      |
+---------+----------+ +---------+----------+ +---------+----------+ +--------------------+
          |                      |                      |
          +----------------------+----------------------+
                                 |
        +------------------------+------------------------+
        |                                                 |
        v (Synchrone OpenFeign)                           v (Asynchrone AMQP)
+-------------------------------+              +-----------------------------------+
|      EUREKA SERVICE DISCOVERY |              |          RABBITMQ BROKER          |
|         (Port 8761)           |              |  Port 5673 (AMQP) / 15673 (Admin) |
+-------------------------------+              +-----------------------------------+
        ^                                                 ^
        |                                                 |
+-------------------------------+              +-----------------------------------+
|      CONFIG SERVER (8888)     |              |     KEYCLOAK IAM (Port 8180)      |
|    Configuration Centralisée  |              |   Realm: skyvoyage (OIDC / JWT)   |
+-------------------------------+              +-----------------------------------+
```



## 🚀 Démarrage Rapide

### 1. Démarrer l'Infrastructure Docker
```bash
# Démarrage des conteneurs (MySQL, RabbitMQ, Keycloak, Redis)
docker compose -f Backend/module2-recherche-consultation/docker-compose.yml up -d
docker run -d --name skyvoyage-redis -p 6379:6379 redis:alpine
```

### 2. Démarrer les Microservices Fondamentaux
```bash
# Terminal 1 : Eureka Server (Port 8761)
cd Backend/discovery && mvn spring-boot:run

# Terminal 2 : Config Server (Port 8888)
cd Backend/config-server && mvn spring-boot:run

# Terminal 3 : API Gateway (Port 8080)
cd Backend/gateway && mvn spring-boot:run

# Terminal 4 : Module 2 Recherche & Consultation (Port 8082)
cd Backend/module2-recherche-consultation && mvn spring-boot:run

# Terminal 5 : Module 1 Gestion Bagages (Port 8081)
cd "Backend/module1-Gestion des bagages" && mvn spring-boot:run
```

### 3. Démarrer le Frontend Angular
```bash
cd Frontend
npm install
npm start
# Accessible sur http://localhost:4200
```

---

## 🔗 Liens Utiles & Interfaces d'Accès

* **Frontend Angular** : [http://localhost:4200](http://localhost:4200)
* **API Gateway** : [http://localhost:8080](http://localhost:8080)
* **Swagger Centralisé** : [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **Eureka Service Discovery** : [http://localhost:8761](http://localhost:8761)
* **Console Keycloak IAM** : [http://localhost:8180](http://localhost:8180) (Admin: `admin` / `admin`)
* **RabbitMQ Management** : [http://localhost:15673](http://localhost:15673) (User: `guest` / `guest`)
* **Config Server Health** : [http://localhost:8888/actuator/health](http://localhost:8888/actuator/health)

---

## 🔐 Identifiants de Démonstration (Keycloak RBAC)

| Rôle | Nom d'utilisateur | Mot de passe | Permissions |
| :--- | :--- | :--- | :--- |
| **Administrateur** | `admin` | `admin` | CRUD complet des offres, ajout/modification/suppression, statistiques et export. |
| **Voyageur** | `voyageur` | `user` | Mode réservation uniquement : recherche multi-critères, consultation, réservation immédiate. |

---

## 📡 Détail des Communications Inter-Services (Note 4)

### Synchrones via OpenFeign
1. **`GET /api/recherche/inter-services/feign/disponibilite/{id}`** : Vérification temps réel de disponibilité (Module 3 FastAPI).
2. **`GET /api/recherche/inter-services/feign/bagages`** : Consultation des franchises bagages et tarifs (Module 1 Spring Boot / Redis).
3. **`GET /api/recherche/inter-services/feign/paiements`** : Vérification des options et statuts de transactions (Module 4).
4. **`GET /api/recherche/inter-services/feign/synthese/{id}`** : Agrégation consolidée multi-microservices pour un voyage.

### Asynchrones via RabbitMQ
1. **`POST /api/recherche/inter-services/rabbitmq/publier-reservation`** : Événement de réservation décrémentant les places et augmentant la popularité.
2. **`POST /api/recherche/inter-services/rabbitmq/publier-paiement`** : Validation asynchrone et confirmation de billet.
3. **`POST /api/recherche/inter-services/rabbitmq/publier-promotion`** : Notification push asynchrone d'une baisse tarifaire.

---

## 🧪 Tests & Qualité
* **Compilation Angular AOT** : `npm run build` (0 erreur).
* **Build Maven Backend** : `mvn clean test package` (0 erreur).
* **Pipeline Automatisé** : GitHub Actions exécuté à chaque commit sur `recherche_consultation`.
