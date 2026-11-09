package com.travel.bagage.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Scénario Feign 2 : Notification et transmission du montant bagage calculé vers le Module 4 Paiement.
 */
@FeignClient(name = "reservation-paiement", url = "${app.services.paiement.url:http://localhost:8084}", fallback = PaymentClientFallback.class)
public interface PaymentServiceClient {

    @PostMapping("/api/paiements/bagages")
    PaymentDto recordBaggagePayment(@RequestBody PaymentDto paymentDto);
}
