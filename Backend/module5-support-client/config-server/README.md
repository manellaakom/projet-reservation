# Spring Cloud Config Server - Module 5 Support Client

Ce service est le **Serveur de Configuration Centralisée (Spring Cloud Config Server)** pour le projet de réservation et plus particulièrement pour le microservice `module5-support-client`.

---

## 🚀 1. Démarrage du Config Server

### Avec Maven Wrapper :
```bash
cd module5-support-client/config-server
./mvnw.cmd spring-boot:run
```
*(ou sur Linux/Mac : `./mvnw spring-boot:run`)*

### Avec le fichier JAR généré :
```bash
cd module5-support-client/config-server
java -jar target/config-server-0.0.1-SNAPSHOT.jar
```

Le serveur démarre sur le port **`8888`**.

---

## 🔍 2. Vérification des Endpoints

Une fois le Config Server lancé, ouvrez votre navigateur ou utilisez `curl` :

### Configuration de Module 5 (Support Client) :
```http
GET http://localhost:8888/module5-support-client/default
```
ou
```http
GET http://localhost:8888/support-client-backend/default
```

### Configuration globale :
```http
GET http://localhost:8888/application/default
```

### Vérification de l'état (Actuator Health) :
```http
GET http://localhost:8888/actuator/health
```

---

## 📁 3. Emplacement des Configurations

Le Config Server est configuré en mode **`native`** (`spring.profiles.active=native`).
Les fichiers de configuration sont situés dans :
- `src/main/resources/config/` (embarqués dans l'application)
- `config-repo/` (dossier local pour modification à chaud)

### Fichiers disponibles :
- `module5-support-client.properties` : Propriétés du backend Symfony (port 8000, DB PostgreSQL, Eureka, CORS)
- `support-client-backend.properties` : Alias pour le nom d'application Eureka
- `application.properties` : Configuration partagée par tous les microservices
- `gateway.properties`, `module1-gestion-bagages.properties`, etc.

---

## 🔌 4. Consommation depuis Symfony (Module 5)

Une commande Symfony est disponible dans le module 5 pour récupérer automatiquement la configuration depuis le Config Server :

```bash
cd module5-support-client
php bin/console app:fetch-config
```

Pour synchroniser directement ces variables dans votre fichier `.env.local` :
```bash
php bin/console app:fetch-config --apply
```
