@echo off
title Arret des services Projet Reservation
echo ==========================================================
echo          ARRET DU PROJET RESERVATION (Module 2)
echo ==========================================================

echo Fermeture des fenetres des microservices...
taskkill /f /fi "WINDOWTITLE eq Eureka Server*" 2>nul
taskkill /f /fi "WINDOWTITLE eq Module 2 Recherche*" 2>nul

echo Termine.
timeout /t 3 >nul
