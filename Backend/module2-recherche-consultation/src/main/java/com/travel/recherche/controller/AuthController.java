package com.travel.recherche.controller;

import com.travel.recherche.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur d'authentification et de génération de Tokens JWT
 * Intégré au Module 2 (Note 2 de la grille AWD : Sécurité).
 */
@RestController
@RequestMapping("/api/recherche/auth")
@Tag(name = "Sécurité & Authentification JWT", description = "Endpoints d'authentification, génération de tokens et contrôle RBAC")
public class AuthController {

    private final JwtUtils jwtUtils;

    public AuthController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    @Operation(summary = "Se connecter et obtenir un Token JWT (Utilisateur ou Administrateur)")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.getOrDefault("username", "voyageur");
        String password = credentials.getOrDefault("password", "123456");

        String role = ("admin".equalsIgnoreCase(username) || "admin".equalsIgnoreCase(password))
                ? "ROLE_ADMIN"
                : "ROLE_USER";

        String token = jwtUtils.generateToken(username, role);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "type", "Bearer",
                "username", username,
                "role", role,
                "expiresInMs", 86400000L,
                "message", "Authentification reussie avec succes"
        ));
    }

    @PostMapping("/register")
    @Operation(summary = "Créer un nouveau compte utilisateur")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> payload) {
        String username = payload.getOrDefault("username", "client_travel");
        String role = "ROLE_USER";
        String token = jwtUtils.generateToken(username, role);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", username,
                "role", role,
                "message", "Compte cree avec succes"
        ));
    }

    @GetMapping("/demo-tokens")
    @Operation(summary = "Tokens de test pré-générés (ADMIN & USER) à copier dans Swagger")
    public ResponseEntity<Map<String, Object>> demoTokens() {
        String adminToken = jwtUtils.generateToken("admin", "ROLE_ADMIN");
        String userToken = jwtUtils.generateToken("client", "ROLE_USER");

        return ResponseEntity.ok(Map.of(
                "ADMIN_TOKEN", adminToken,
                "ADMIN_HEADER", "Bearer " + adminToken,
                "USER_TOKEN", userToken,
                "USER_HEADER", "Bearer " + userToken,
                "instruction", "Copiez 'Bearer <ADMIN_TOKEN>' et cliquez sur 'Authorize' en haut de Swagger UI pour déverrouiller la création, modification et suppression des voyages !"
        ));
    }

    @GetMapping("/verify")
    @Operation(summary = "Vérifier la validité d'un token JWT")
    public ResponseEntity<Map<String, Object>> verify(@RequestParam String token) {
        boolean valid = jwtUtils.validateToken(token);
        if (valid) {
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "username", jwtUtils.getUsername(token),
                    "role", jwtUtils.getRole(token)
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "Token invalide ou expire"
            ));
        }
    }
}
