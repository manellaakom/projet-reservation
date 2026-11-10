package com.travel.recherche.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Client OpenFeign compatible avec le Module 3 (gestion-reservation FastAPI)
 * Nom Eureka configuré dans Module 3 (app/core/config.py) : RESERVATION-SERVICE sur le port 8003
 */
@FeignClient(
        name = "RESERVATION-SERVICE",
        url = "${services.reservation.url:http://localhost:8003}"
)
public interface ReservationDisponibiliteClient {

    @GetMapping("/api/v1/disponibilites/voyage/{idVoyage}")
    Map<String, Object> getDisponibiliteByVoyage(@PathVariable("idVoyage") Long idVoyage);
}
