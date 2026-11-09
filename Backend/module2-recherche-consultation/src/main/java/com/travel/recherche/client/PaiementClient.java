package com.travel.recherche.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * Client OpenFeign pour le Module 4 (reservation-paiement) - Port 8084
 * Communication synchrone inter-microservices (Scénario 3)
 */
@FeignClient(
        name = "reservation-paiement",
        url = "${services.paiement.url:http://localhost:8084}"
)
public interface PaiementClient {

    @GetMapping("/api/paiements")
    List<Map<String, Object>> getAllPaiements();

    @GetMapping("/api/paiements/{id}")
    Map<String, Object> getPaiementById(@PathVariable("id") String id);
}
