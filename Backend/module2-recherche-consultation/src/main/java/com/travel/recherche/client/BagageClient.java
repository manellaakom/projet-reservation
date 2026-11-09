package com.travel.recherche.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * Client OpenFeign pour le Module 1 (gestion-bagages) - Port 8081
 * Communication synchrone inter-microservices (Scénario 2)
 */
@FeignClient(
        name = "gestion-bagages",
        url = "${services.bagages.url:http://localhost:8081}"
)
public interface BagageClient {

    @GetMapping("/api/bagages")
    List<Map<String, Object>> getAllBagages();

    @GetMapping("/api/bagages/{id}")
    Map<String, Object> getBagageById(@PathVariable("id") String id);

    @GetMapping("/api/bagages/statistics")
    Map<String, Object> getBagageStatistics();
}
