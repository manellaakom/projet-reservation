# Eureka Server

Registre de services pour les microservices AWD. Le dashboard est disponible sur <http://localhost:8761>.

## Démarrage

Depuis ce dossier, avec Java 17 et Maven installés :

```powershell
mvn spring-boot:run
```

Démarrez ensuite les microservices clients. L'URL du registre est `http://localhost:8761/eureka/`.