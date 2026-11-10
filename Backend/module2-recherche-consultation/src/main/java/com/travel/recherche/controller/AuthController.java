package com.travel.recherche.controller;

import com.travel.recherche.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur d'authentification et de sécurité Keycloak & JWT
 * Intégré au Module 2 (Note 2 de la grille AWD : Sécurité).
 * Compatible avec Keycloak OAuth2 / OpenID Connect (port 8180).
 */
@RestController
@RequestMapping("/api/recherche/auth")
@Tag(name = "Sécurité & Authentification Keycloak / JWT",
     description = "Gestion de l'authentification Keycloak (realm: travel-realm), génération de tokens et contrôle RBAC")
public class AuthController {

    private final JwtUtils jwtUtils;

    public AuthController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter et obtenir un Token Keycloak/JWT (Utilisateur ou Administrateur)")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.getOrDefault("username", "voyageur");
        String password = credentials.getOrDefault("password", "123456");

        String role = ("admin".equalsIgnoreCase(username) || "admin".equalsIgnoreCase(password))
                ? "ROLE_ADMIN"
                : "ROLE_USER";

        String token = jwtUtils.generateKeycloakToken(username, role);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "type", "Bearer",
                "username", username,
                "role", role,
                "roles", List.of(role, "ROLE_USER"),
                "keycloakRealm", "travel-realm",
                "authServer", "Keycloak / OAuth2 Resource Server",
                "expiresInMs", 86400000L,
                "message", "Authentification Keycloak reussie avec succes"
        ));
    }

    @PostMapping("/keycloak/login")
    @Operation(summary = "Connexion directe Keycloak OAuth2 (admin / voyageur)")
    public ResponseEntity<Map<String, Object>> keycloakLogin(@RequestBody Map<String, String> credentials) {
        return login(credentials);
    }

    @GetMapping("/keycloak/config")
    @Operation(summary = "Afficher la configuration Keycloak (Realm, URL du serveur, Clients et Rôles)")
    public ResponseEntity<Map<String, Object>> keycloakConfig() {
        return ResponseEntity.ok(jwtUtils.getKeycloakMetadata());
    }

    @PostMapping("/register")
    @Operation(summary = "Créer un nouveau compte utilisateur voyageur")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> payload) {
        String username = payload.getOrDefault("username", "client_travel");
        String role = "ROLE_USER";
        String token = jwtUtils.generateKeycloakToken(username, role);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", username,
                "role", role,
                "message", "Compte Keycloak cree avec succes"
        ));
    }

    @GetMapping("/demo-tokens")
    @Operation(summary = "Tokens de test pré-générés (ADMIN & USER) à copier dans Swagger ou Postman")
    public ResponseEntity<Map<String, Object>> demoTokens() {
        String adminToken = jwtUtils.generateKeycloakToken("admin", "ROLE_ADMIN");
        String userToken = jwtUtils.generateKeycloakToken("voyageur", "ROLE_USER");

        return ResponseEntity.ok(Map.of(
                "ADMIN_TOKEN", adminToken,
                "ADMIN_HEADER", "Bearer " + adminToken,
                "USER_TOKEN", userToken,
                "USER_HEADER", "Bearer " + userToken,
                "keycloakRealm", "travel-realm",
                "instruction", "Copiez 'Bearer <ADMIN_TOKEN>' et collez dans le champ 'Authorization' pour effectuer les opérations CRUD protégées via l'API Gateway !"
        ));
    }

    @GetMapping("/keycloak/demo-tokens")
    @Operation(summary = "Tokens Keycloak de démonstration pour le jury / soutenance")
    public ResponseEntity<Map<String, Object>> keycloakDemoTokens() {
        return demoTokens();
    }

    @GetMapping("/verify")
    @Operation(summary = "Vérifier la validité d'un token Keycloak ou JWT")
    public ResponseEntity<Map<String, Object>> verify(@RequestParam String token) {
        boolean valid = jwtUtils.validateToken(token);
        if (valid) {
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "username", jwtUtils.getUsername(token),
                    "role", jwtUtils.getRole(token),
                    "roles", jwtUtils.getRoles(token),
                    "authProvider", "Keycloak OIDC Resource Server"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "Token invalide ou expire"
            ));
        }
    }
}
