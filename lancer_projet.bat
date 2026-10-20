@echo off
title Lancement du projet Reservation (Module 2)
echo ==========================================================
echo       DEMARRAGE DU PROJET RESERVATION (Module 2)
echo ==========================================================

set JAVA_CMD="C:\Users\Nour\AppData\Local\Programs\IntelliJ IDEA 2026.2.3\jbr\bin\java.exe"
set BASE_DIR=%~dp0

echo [1/3] Verification et demarrage d'Eureka Discovery (Port 8761)...
start "Eureka Server [8761]" %JAVA_CMD% -jar "%BASE_DIR%Backend\discovery\target\discovery-0.0.1-SNAPSHOT.jar"

echo En attente d'Eureka (5 secondes)...
timeout /t 5 /nobreak >nul

echo [2/3] Demarrage du Microservice Recherche-Consultation (Port 8082)...
start "Module 2 Recherche [8082]" %JAVA_CMD% -jar "%BASE_DIR%Backend\module2-recherche-consultation\target\module2-recherche-consultation-0.0.1-SNAPSHOT.jar"

echo En attente du demarrage du backend (6 secondes)...
timeout /t 6 /nobreak >nul

echo [3/3] Ouverture de l'application Frontend dans le navigateur...
start http://localhost/projet-reservation/index.html

echo ==========================================================
echo Tous les services sont lances !
echo - Frontend Web        : http://localhost/projet-reservation/index.html
echo - Backend API REST    : http://localhost:8082/api/recherche/voyages
echo - Swagger UI Docs     : http://localhost:8082/swagger-ui.html
echo - Eureka Server       : http://localhost:8761
echo - Base de donnees     : MySQL / phpMyAdmin sur port 3306
echo ==========================================================
pause
