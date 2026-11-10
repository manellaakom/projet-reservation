# Module 5 - Support Client (Microservice PHP/Symfony)

Ce module gère les demandes de support client et les réclamations pour l'application de réservation **SkyVoyage**. Il fait partie d'une architecture microservices où les autres modules sont développés en Java (Spring Boot).

## 🚀 Technologies utilisées
*   **Backend** : PHP 8.4 / Symfony 7
*   **Base de données** : PostgreSQL 16
*   **Sécurité** : JWT (LexikJWTAuthenticationBundle)
*   **API** : API Platform (Swagger)
*   **Conteneurisation** : Docker & Docker Compose

## 🛠️ Comment lancer le projet

Vous avez deux options pour lancer ce module : avec Docker (recommandé) ou en local.

### Option 1 : Avec Docker (Recommandé)
Cette méthode lance l'application, la base de données et le service de mail dans des conteneurs isolés.

1.  Assurez-vous que **Docker Desktop** est installé et lancé.
2.  À la racine du projet, lancez la commande suivante pour construire et démarrer les conteneurs :
    ```bash
    docker compose up -d --build