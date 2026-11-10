package com.travel.bagage.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Scénario Feign 1 : Consultation et validation des réservations auprès du Module 3.
 */
@FeignClient(name = "gestion-reservation", url = "${app.services.reservation.url:http://localhost:8003}", fallback = ReservationClientFallback.class)
public interface ReservationServiceClient {

    @GetMapping("/api/v1/reservations/{reservationId}")
    ReservationDto getReservationById(@PathVariable("reservationId") String reservationId);
}
