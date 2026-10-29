@echo off
title Arret des services Projet Reservation
echo ==========================================================
echo          ARRET DU PROJET RESERVATION (Module 2)
echo ==========================================================

echo [1/2] Fermeture des fenetres des microservices...
taskkill /f /fi "WINDOWTITLE eq Eureka Server*" 2>nul
taskkill /f /fi "WINDOWTITLE eq Module 2 Recherche*" 2>nul
taskkill /f /fi "WINDOWTITLE eq API Gateway*" 2>nul

echo [2/2] Nettoyage des ports 8761, 8082, 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8761 " ^| findstr LISTENING') do taskkill /f /pid %%a 2>nul
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8082 " ^| findstr LISTENING') do taskkill /f /pid %%a 2>nul
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080 " ^| findstr LISTENING') do taskkill /f /pid %%a 2>nul

echo ==========================================================
echo Tous les microservices ont ete arretes proprement !
echo ==========================================================
timeout /t 3 >nul
