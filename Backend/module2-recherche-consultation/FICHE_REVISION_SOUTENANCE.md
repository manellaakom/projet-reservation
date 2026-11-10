# 🎓 Fiche de Révision Complète — Module 2 : Recherche & Consultation

Ce document est votre guide pour préparer et réussir votre soutenance technique (évaluation AWD). Il synthétise l'architecture, le code, les mécanismes avancés et **l'ensemble des commandes utilisées**.

---

## 📌 1. Fiche d'Identité du Microservice

| Caractéristique | Valeur |
|---|---|
| **Nom du projet** | `module2-recherche-consultation` |
| **Framework** | Spring Boot 3.2.4 / Java 17 |
| **Port natif** | `8082` |
| **Port conteneurisé (Docker)** | `9082` (mappé vers `8082` interne) |
| **Base de données** | MySQL (`recherche_consultation_db` sur port 3306) |
| **Broker de messages** | RabbitMQ (port AMQP 5672, UI 15672) |
| **Service Discovery** | Spring Cloud Netflix Eureka (`localhost:8761`) |
| **Documentation API** | Swagger UI (`/swagger-ui.html` & `/v3/api-docs`) |
| **Monitoring** | Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`) |
| **Branche Git** | `recherche_consultation` |

---

## 🏗️ 2. Les 6 Piliers Techniques (Alignés sur la Grille d'Évaluation)

### Pilier 1 : Entité `Voyage` & CRUD Complet
* **Fichiers clés** :
  * `src/main/java/com/travel/recherche/entity/Voyage.java` : Entité JPA avec contraintes d'intégrité (`codeVoyage` unique, `@NotBlank`, `@Min`, énumérations).
  * `src/main/java/com/travel/recherche/controller/RechercheConsultationController.java` : Contrôleur REST exposant l'ensemble des fonctionnalités.
* **Fonctionnalités avancées** :
  * **Recherche multi-critères** : Filtrage dynamique par ville, pays, compagnie, dates, type de transport, fourchette de prix, bagages inclus, vol direct.
  * **Tri intelligent** : Tri dynamique (`prix`, `dateDepart`, `noteMoyenne`) en ordre croissant/décroissant (`asc`/`desc`).
  * **Comparateur d'offres** : Endpoint `/comparer?ids=1,2` comparant 2 à 5 voyages côte-à-côte (calcul automatique de l'écart de prix et des avantages).
  * **Moteur de suggestions** : Endpoint `/suggestions` calculant les offres tendances, les promotions et les petits budgets.
  * **Statistiques globales** : Endpoint `/statistiques` fournissant les métriques agrégées du catalogue.

---

### Pilier 2 : Sécurité Spring Security & JWT (Note 2 de la Grille)
* **Fichiers clés** :
  * `src/main/java/com/travel/recherche/security/SecurityConfig.java`
  * `src/main/java/com/travel/recherche/security/JwtAuthenticationFilter.java`
  * `src/main/java/com/travel/recherche/security/JwtUtils.java`
* **Règles d'accès (RBAC)** :
  * **Public (sans token)** :
    * Consultation catalogue : `GET /api/recherche/**`
    * Recherche POST : `POST /api/recherche/voyages/search`
    * Comparateur POST : `POST /api/recherche/comparer`
    * Swagger UI & Actuator : `/swagger-ui/**`, `/v3/api-docs/**`, `/actuator/**`
  * **Authentifié (`ROLE_USER` ou `ROLE_ADMIN`)** :
    * Création d'offre : `POST /api/recherche/voyages`
    * Modification d'offre : `PUT /api/recherche/voyages/{id}`
  * **Réservé exclusivement à `ROLE_ADMIN`** :
    * Suppression d'offre : `DELETE /api/recherche/voyages/{id}` (vérification via `.hasRole("ADMIN")`)

> Le filtre `JwtAuthenticationFilter` est conçu pour tolérer aussi bien le token brut que le token préfixé `Bearer `, évitant ainsi le piège du double `Bearer Bearer` généré par Swagger UI.

---

### Pilier 3 : Communication Inter-Microservices (Note 4 de la Grille)

#### A. Communication Synchrone via OpenFeign
* **Clients Feign** :
  * `src/main/java/com/travel/recherche/client/BagageClient.java` : Appelle le microservice Bagages (`http://localhost:8081`) pour récupérer le barème tarifaire et vérifier le poids autorisé.
  * `src/main/java/com/travel/recherche/client/PaiementClient.java` : Appelle le microservice Paiement (`http://localhost:8084`) pour vérifier le statut des transactions.

#### B. Communication Asynchrone via RabbitMQ
* **Fichiers** :
  * `src/main/java/com/travel/recherche/config/RabbitMQConfig.java` : Déclaration du Topic Exchange `travel.exchange`, de la file `voyage.recherche.queue` et du `Jackson2JsonMessageConverter`.
  * `src/main/java/com/travel/recherche/event/TravelEventProducer.java` : Émet des événements (`voyage.promotion`, `voyage.cree`) vers les autres microservices.
  * `src/main/java/com/travel/recherche/event/TravelEventConsumer.java` : Écoute les réservations créées pour décrémenter automatiquement le stock de places disponibles en temps réel.

---

### Pilier 4 : Conteneurisation Docker
* **Fichiers clés** :
  * `Dockerfile` : Image légère basée sur `eclipse-temurin:17-jre-alpine` (~430 Mo tout compris).
  * `docker-compose.yml` : Orchestration autonome.
* **Astuce réseau Windows** : `host.docker.internal` est utilisé comme hôte pour MySQL (`3306`) et RabbitMQ (`5672`), ce qui permet au conteneur de dialoguer avec les services installés sur Windows (XAMPP / broker local).

---

## 💻 3. Guide de Toutes les Commandes Utilisées

### ☕ A. Compilation & Packaging Maven
À exécuter dans `Backend/module2-recherche-consultation/` :
```powershell
# Définir le JDK 17
$env:JAVA_HOME = "C:\Users\Nour\AppData\Local\Programs\IntelliJ IDEA 2026.2.3\jbr"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Compiler et générer le JAR exécutable (en ignorant les tests unitaires H2)
.\mvnw.cmd clean package -DskipTests
```
* **Fichier produit** : `target/module2-recherche-consultation-0.0.1-SNAPSHOT.jar`

---

### 🐳 B. Gestion Docker & Conteneurs

#### 1. Préparation de l'environnement Docker CLI :
```powershell
$env:PATH = "C:\Users\Nour\AppData\Local\Programs\DockerDesktop\resources\bin;$env:PATH"
```

#### 2. Construction de l'image Docker :
```powershell
docker build -t module2-recherche:latest .
```

#### 3. Lancement du conteneur avec variables d'environnement réseau hôte :
```powershell
# Supprimer tout conteneur de test précédent
docker rm -f module2-docker-test

# Lancer sur le port 9082
docker run -d --name module2-docker-test -p 9082:8082 `
  -e "SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/recherche_consultation_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" `
  -e "RABBITMQ_HOST=host.docker.internal" `
  -e "SPRING_RABBITMQ_HOST=host.docker.internal" `
  -e "EUREKA_CLIENT_REGISTER_WITH_EUREKA=false" `
  -e "EUREKA_CLIENT_FETCH_REGISTRY=false" `
  module2-recherche:latest
```

#### 4. Suivi et inspection du conteneur :
```powershell
# Voir l'état du conteneur
docker ps --filter "name=module2-docker-test"

# Voir les logs en direct (défilement en temps réel)
docker logs -f module2-docker-test

# Ouvrir un terminal à l'intérieur du conteneur Linux
docker exec -it module2-docker-test sh
```

---

### 🌿 C. Commandes Git & Versioning
Exécutées via WSL Ubuntu sur le dépôt :
```bash
# Vérifier l'état de la branche
wsl -d Ubuntu git -C /mnt/c/Users/Nour/OneDrive/Desktop/5eme/web/projet-reservation status

# Voir l'historique récent des commits
wsl -d Ubuntu git -C /mnt/c/Users/Nour/OneDrive/Desktop/5eme/web/projet-reservation log -n 5 --oneline

# Pousser les modifications sur le serveur distant
wsl -d Ubuntu git -C /mnt/c/Users/Nour/OneDrive/Desktop/5eme/web/projet-reservation push origin recherche_consultation
```

---

### 🧪 D. Commandes de Test & Validation des Endpoints (PowerShell)

#### 1. Vérifier Actuator Health (Santé du conteneur, DB et RabbitMQ) :
```powershell
Invoke-RestMethod -Uri "http://localhost:9082/actuator/health"
```

#### 2. Recherche publique sans token (Doit réussir avec HTTP 200) :
```powershell
$body = @{ villeDestination = "Paris"; prixMax = 500 } | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:9082/api/recherche/voyages/search" `
  -Method Post -ContentType "application/json" -Body $body
```

#### 3. Test de Sécurité — Création sans token (Doit être rejetée avec HTTP 403) :
```powershell
$voyage = @{
    codeVoyage = "TEST-SEC-01"
    titre = "Test Securite"
    villeDepart = "Tunis"
    villeDestination = "Rome"
    paysDestination = "Italie"
    compagnie = "Tunisair"
    typeTransport = "VOL"
    categorie = "CONFORT"
    dateDepart = "2026-12-01T10:00:00"
    dateArrivee = "2026-12-01T12:00:00"
    dureeMinutes = 120
    prixBase = 300.0
    placesDisponibles = 10
} | ConvertTo-Json

try {
    Invoke-WebRequest -Uri "http://localhost:9082/api/recherche/voyages" `
      -Method Post -ContentType "application/json" -Body $voyage -UseBasicParsing
} catch {
    Write-Host "Rejet confirme :" $_.Exception.Response.StatusCode.value__
}
```

#### 4. Test de Sécurité — Création avec Token ADMIN (Doit réussir avec HTTP 201) :
```powershell
$adminToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJST0xFX0FETUlOIiwiaWF0IjoxNzk0MjQxNzgxLCJleHAiOjE3OTQzMjgxODF9.YhANGhMFuQAcBWtRN1Nv5-jNP4LIzUok9h-V7btpUaA"

$utf8Bytes = [System.Text.Encoding]::UTF8.GetBytes($voyage)
$resp = Invoke-RestMethod -Uri "http://localhost:9082/api/recherche/voyages" `
  -Method Post -ContentType "application/json; charset=utf-8" `
  -Headers @{ Authorization = "Bearer $adminToken" } -Body $utf8Bytes

Write-Host "Voyage cree avec succes ! ID =" $resp.id
```

#### 5. Test RBAC — Suppression avec `ROLE_USER` (403) vs `ROLE_ADMIN` (204) :
```powershell
$userToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnQiLCJyb2xlIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzk0MjQxNzgxLCJleHAiOjE3OTQzMjgxODF9.AGJxFptfrrXAp5JQh4IcjlCT4esJ10LmFUPg2Z77nzc"

# Tentative avec ROLE_USER -> Rejet 403
try {
    Invoke-WebRequest -Uri "http://localhost:9082/api/recherche/voyages/$($resp.id)" `
      -Method Delete -Headers @{ Authorization = "Bearer $userToken" } -UseBasicParsing
} catch {
    Write-Host "Suppression interdite a ROLE_USER :" $_.Exception.Response.StatusCode.value__
}

# Suppression par ROLE_ADMIN -> Reussite 204
$del = Invoke-WebRequest -Uri "http://localhost:9082/api/recherche/voyages/$($resp.id)" `
  -Method Delete -Headers @{ Authorization = "Bearer $adminToken" } -UseBasicParsing
Write-Host "Suppression reussie par ROLE_ADMIN : HTTP" $del.StatusCode
```

---

## 🎤 4. FAQ / Questions Typiques du Jury en Soutenance

### Q1 : « Pourquoi avoir utilisé Docker et comment le conteneur accède-t-il à MySQL ? »
> **Réponse** :  
> Le Dockerfile utilise une image minimale Alpine Temurin Java 17, ce qui garantit portabilité, isolation et légèreté. Sous Docker Desktop Windows, l'adresse spéciale `host.docker.internal` permet au conteneur de pointer directement vers le serveur MySQL hôte (`3306`) et le broker RabbitMQ hôte (`5672`), garantissant une intégration sans friction.

### Q2 : « Comment fonctionne la sécurité dans votre microservice ? »
> **Réponse** :  
> Nous avons une architecture stateless basée sur Spring Security 6 et JWT (`JwtAuthenticationFilter`). Les requêtes de consultation (`GET`) et de recherche (`POST /search`) sont publiques pour les clients. Les modifications nécessitent un token valide, et la suppression (`DELETE`) exige explicitement le rôle `ROLE_ADMIN`. Swagger UI est configuré avec un schéma Bearer Security pour tester directement avec nos jetons.

### Q3 : « Pourquoi combiner OpenFeign et RabbitMQ ? »
> **Réponse** :  
> - **OpenFeign** est idéal pour la communication **synchrone** immédiate en temps réel (ex: vérifier instantanément le prix des bagages ou l'état d'un paiement).  
> - **RabbitMQ** est utilisé pour le découplage **asynchrone** (ex: propager un événement de réservation pour mettre à jour le nombre de places disponibles sans bloquer la requête utilisateur).

### Q4 : « Quels composants de valeur ajoutée avez-vous intégrés ? »
> **Réponse** :  
> 1. Un **moteur de suggestions** intelligent (`/suggestions`).  
> 2. Un **comparateur multi-offres** (`/comparer`).  
> 3. L'observabilité via **Spring Boot Actuator** (`/actuator/health`).  
> 4. Une documentation interactive **OpenAPI 3 / Swagger UI** avec persistance de session d'authentification.
