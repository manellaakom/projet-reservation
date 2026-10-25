package com.travel.recherche.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@Hidden
public class RootAndActuatorController {

    @GetMapping("/")
    public RedirectView root() {
        return new RedirectView("/swagger-ui/index.html");
    }

    @GetMapping("/actuator/info")
    public ResponseEntity<Map<String, Object>> actuatorInfo() {
        return ResponseEntity.ok(Map.of(
                "app", "recherche-consultation",
                "service", "module2-recherche-consultation",
                "entite", "Voyage",
                "version", "1.0.0",
                "description", "Microservice Recherche et Consultation d'Offres de Voyage",
                "status", "UP"
        ));
    }

    @GetMapping("/actuator/health")
    public ResponseEntity<Map<String, String>> actuatorHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "UP"
        ));
    }
}
