\# Module Support Client — API REST Symfony



Backend API REST du \*\*module Support Client\*\* d'une plateforme de réservation de voyages et de billets. Ce module fait partie d'une architecture microservices et gère la communication entre les voyageurs et le service client.



\---



\## 📋 Table des matières



\- \[Description](#-description)

\- \[Architecture](#-architecture)

\- \[Technologies](#-technologies)

\- \[Installation](#-installation)

\- \[Configuration](#-configuration)

\- \[Lancement](#-lancement)

\- \[API REST](#-api-rest)

\- \[Notifications Email](#-notifications-email)

\- \[Upload de fichiers](#-upload-de-fichiers)

\- \[Tests](#-tests)

\- \[Structure du projet](#-structure-du-projet)



\---



\## 🎯 Description



Ce module permet aux \*\*clients\*\* de créer des demandes de support et aux \*\*agents\*\* d'y répondre. Il gère :



\- La \*\*création\*\* de demandes de support

\- La \*\*catégorisation\*\* des demandes

\- Le \*\*suivi du statut\*\* (En cours / Résolu / Clôturé)

\- L'\*\*ajout de pièces jointes\*\* (max 10 Mo)

\- Les \*\*réponses\*\* aux demandes

\- Les \*\*notifications email\*\* automatiques aux changements de statut



\---



\## 🏗️ Architecture



Ce module fait partie de l'architecture microservices globale :



