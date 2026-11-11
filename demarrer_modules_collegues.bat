@echo off
title Demarrage des Microservices Collegues (Modules 2, 3, 4, 5)
echo ====================================================================
echo       DEMARRAGE DES MODULES COLLEGUES (SKYVOYAGE)
echo ====================================================================

set NETWORK=projet-reservation-bagages-clean_skyvoyage-net

echo [1/6] Verification du reseau Docker...
docker network inspect %NETWORK% >nul 2>&1
if %errorlevel% neq 0 (
    set NETWORK=skyvoyage-net
)

echo [2/6] Demarrage de la base de donnees MySQL (pour Modules 2 et 3)...
docker start skyvoyage-mysql 2>nul || docker run -d --name skyvoyage-mysql --network %NETWORK% -p 3307:3306 -e MYSQL_ALLOW_EMPTY_PASSWORD=yes -e MYSQL_DATABASE=recherche_consultation_db -v skyvoyage-mysql-data:/var/lib/mysql -v "%~dp0Backend\module2-recherche-consultation\database.sql:/docker-entrypoint-initdb.d/init.sql:ro" mysql:8.0

echo [3/6] Demarrage de la base de donnees PostgreSQL (pour Module 5)...
docker start skyvoyage-postgres 2>nul || docker run -d --name skyvoyage-postgres --network %NETWORK% -p 5432:5432 -e POSTGRES_DB=projet_reservation -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres123 -v skyvoyage-postgres-data:/var/lib/postgresql/data postgres:16-alpine

echo [4/6] Demarrage Module 2 - Recherche et Consultation (Port 8082)...
docker rm -f skyvoyage-recherche-consultation 2>nul
docker run -d --name skyvoyage-recherche-consultation --network %NETWORK% -p 8082:8082 -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/recherche_consultation_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD="" -e SPRING_RABBITMQ_HOST=rabbitmq -e SPRING_RABBITMQ_PORT=5672 -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="http://discovery:8761/eureka/" -e EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE="http://discovery:8761/eureka/" -e EUREKA_INSTANCE_PREFER_IP_ADDRESS=true projet-reservation-main-recherche-consultation:latest

echo [5/6] Demarrage Module 3 - Gestion des Reservations (Port 8003)...
docker rm -f skyvoyage-gestion-reservation 2>nul
docker run -d --name skyvoyage-gestion-reservation --network %NETWORK% -p 8003:8003 -e DB_HOST=mysql -e DB_PORT=3306 -e DB_USER=root -e DB_PASSWORD="" -e DB_NAME=reservation_db -e DB_AUTO_CREATE=true -e EUREKA_ENABLED=true -e EUREKA_SERVER_URL="http://discovery:8761/eureka/" -e EUREKA_INSTANCE_HOST=gestion-reservation -e EUREKA_INSTANCE_PORT=8003 -e EUREKA_APP_NAME=RESERVATION-SERVICE projet-reservation-main-gestion-reservation:latest

echo [6/6] Demarrage Module 4 - Reservation et Paiement (Port 8084)...
docker rm -f skyvoyage-reservation-paiement 2>nul
docker run -d --name skyvoyage-reservation-paiement --network %NETWORK% -p 8084:8084 -e REDIS_HOST=redis -e REDIS_PORT=6379 -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="http://discovery:8761/eureka/" -e EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE="http://discovery:8761/eureka/" -e EUREKA_INSTANCE_PREFER_IP_ADDRESS=true projet-reservation-main-reservation-paiement:latest

echo [7/7] Demarrage Module 5 - Support Client (Port 8000)...
docker rm -f skyvoyage-support-client 2>nul
docker run -d --name skyvoyage-support-client --network %NETWORK% -p 8000:80 -e DATABASE_URL="postgresql://postgres:postgres123@postgres:5432/projet_reservation?serverVersion=16&charset=utf8" -e EUREKA_SERVER_URL="http://discovery:8761/eureka" -e EUREKA_APP_NAME=SUPPORT-CLIENT-BACKEND -e EUREKA_HOST=support-client -e EUREKA_PORT=80 projet-reservation-main-support-client:latest

echo ====================================================================
echo En attente de l'enregistrement Eureka (15 secondes)...
timeout /t 15 /nobreak >nul
echo ====================================================================
echo Services en cours d'execution :
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ====================================================================
echo Verifiez maintenant Eureka sur : http://localhost:8761
echo ====================================================================
pause
