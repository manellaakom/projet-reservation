# SkyVoyage - Réservations et paiements

Interface Angular pour l’espace voyageur SkyVoyage. Le tableau de bord présente les paiements et leurs statuts. Le module Paiements permet de consulter, rechercher, filtrer, créer, modifier et supprimer des paiements via l’API du backend.

## Démarrage

Prérequis : Node.js 20.19 ou plus récent, npm et Docker Desktop (pour Redis).

1. Démarrez Docker Desktop, puis Redis depuis la racine du dépôt :

```powershell
docker compose -f Backend/docker-compose.yml up -d redis keycloak
```

2. Démarrez le serveur Eureka :

```powershell
Set-Location Backend/eureka
.\mvnw.cmd spring-boot:run
```

3. Dans un autre terminal, démarrez le service Paiement :

```powershell
Set-Location Backend/module4-reservation-paiement
.\mvnw.cmd spring-boot:run
```

4. Dans un autre terminal, démarrez le gateway :

```powershell
Set-Location Backend/gateway
.\mvnw.cmd spring-boot:run
```

5. Depuis `Frontend`, démarrez Angular :

```powershell
npm install
npm start
```

Ouvrez `http://localhost:4200`. Le proxy Angular redirige `/api` vers le gateway `http://localhost:8088`, qui expose les paiements sur `/api/paiements`.

Au premier accès, Keycloak affiche la connexion. Créez un utilisateur dans la
console Keycloak (`http://localhost:8180`, compte local `admin` / `admin`),
puis connectez-vous dans SkyVoyage. Le gateway refuse les appels API sans
jeton Bearer. Le compte administrateur indiqué est réservé au développement
local et ne doit pas être utilisé en production.

Le port `8088` est utilisé car le port standard `8080` est occupé par un autre service sur cette machine. Le service Redis doit également être démarré sur le port `6379` pour enregistrer durablement les paiements.

Si Redis utilise une autre adresse, un autre port ou un mot de passe, configurez le service Paiement avant son démarrage :

```powershell
$env:REDIS_HOST = "localhost"
$env:REDIS_PORT = "6379"
$env:REDIS_PASSWORD = "<mot de passe Redis si nécessaire>"
```

## Paiement en ligne Paymee

Le front redirige vers le checkout hébergé de Paymee ; les données de carte ne transitent jamais par l’application. Le backend utilise le sandbox par défaut. Avant d’accepter un paiement, configurez les variables suivantes dans le terminal qui lance le service Paiement :

```powershell
$env:PAYMEE_API_KEY = "<votre clé sandbox Paymee>"
$env:PAYMEE_BASE_URL = "https://sandbox.paymee.tn"
$env:PAYMEE_FRONTEND_BASE_URL = "https://<adresse-HTTPS-publique-du-front>"
$env:PAYMEE_WEBHOOK_URL = "https://<adresse-HTTPS-publique-de-l-api>/api/paiements/paymee/webhook"
```

Le front et le gateway/API doivent avoir chacun une adresse HTTPS joignable par Paymee (par exemple via un tunnel HTTPS pendant les essais). Ne mettez jamais la clé dans Angular ou dans Git. Pour la production, Paymee doit activer le compte marchand et vous fournir sa clé ; utilisez alors `PAYMEE_BASE_URL=https://app.paymee.tn`.

Le webhook est authentifié avec la somme de contrôle Paymee puis le backend vérifie le statut et le montant directement auprès de Paymee avant de marquer le paiement comme réglé. Le retour navigateur seul ne confirme jamais le paiement.

Le service des réservations est présenté dans l’interface, mais ses routes FastAPI sont encore à implémenter dans le backend (`/api/v1/reservations`). Le front indique cette limite et ne crée pas de réservations fictives. La documentation FastAPI est accessible sur `http://localhost:8003/docs` lorsque le service est démarré.

Chaque microservice Java peut être lancé avec `.\mvnw.cmd spring-boot:run` depuis son dossier.

## Commandes

- `npm start` : démarre Angular avec le proxy backend.
- `npm run build` : construit l’application de production dans `dist/`.
