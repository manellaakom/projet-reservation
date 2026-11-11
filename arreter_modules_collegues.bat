@echo off
title Arret des Microservices Collegues
echo Arret des modules 2, 3, 4, 5 et des bases de donnees...
docker stop skyvoyage-recherche-consultation skyvoyage-gestion-reservation skyvoyage-reservation-paiement skyvoyage-support-client skyvoyage-mysql skyvoyage-postgres
echo Termine.
pause
