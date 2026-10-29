@echo off
title Lancement du projet Reservation (Module 2)
echo ==========================================================
echo       DEMARRAGE DU PROJET RESERVATION (Module 2)
echo ==========================================================

set JAVA_CMD="C:\Users\Nour\AppData\Local\Programs\IntelliJ IDEA 2026.2.3\jbr\bin\java.exe"
set BASE_DIR=%~dp0

echo [Nettoyage] Liberation des ports si deja occupes...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8761 " ^| findstr LISTENING') do taskkill /f /pid %%a 2>nul
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8082 " ^| findstr LISTENING') do taskkill /f /pid %%a 2>nul
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080 " ^| findstr LISTENING') do taskkill /f /pid %%a 2>nul

echo [1/4] Verification et demarrage d'Eureka Discovery (Port 8761)...
start "Eureka Server [8761]" %JAVA_CMD% -jar "%BASE_DIR%Backend\discovery\target\discovery-0.0.1-SNAPSHOT.jar"

echo En attente d'Eureka (5 secondes)...
timeout /t 5 /nobreak >nul

echo [2/4] Demarrage du Microservice Recherche-Consultation (Port 8082)...
start "Module 2 Recherche [8082]" %JAVA_CMD% -jar "%BASE_DIR%Backend\module2-recherche-consultation\target\module2-recherche-consultation-0.0.1-SNAPSHOT.jar"

echo En attente du demarrage du microservice (6 secondes)...
timeout /t 6 /nobreak >nul

echo [3/4] Demarrage de l'API Gateway (Port 8080)...
start "API Gateway [8080]" %JAVA_CMD% -jar "%BASE_DIR%Backend\gateway\target\gateway-0.0.1-SNAPSHOT.jar"

echo En attente de la Gateway (5 secondes)...
timeout /t 5 /nobreak >nul

echo [4/4] Ouverture du Frontend et de Swagger...
start http://localhost/projet-reservation/index.html
start http://localhost:8082/swagger-ui.html

echo ==========================================================
echo Tous les services sont lances avec succes !
echo - Frontend Web        : http://localhost/projet-reservation/index.html
echo - API Gateway         : http://localhost:8080/api/recherche/voyages
echo - Backend API REST    : http://localhost:8082/api/recherche/voyages
echo - Swagger UI Docs     : http://localhost:8082/swagger-ui.html
echo - Eureka Server       : http://localhost:8761
echo - Base de donnees     : MySQL / phpMyAdmin sur port 3306
echo ==========================================================
pause
