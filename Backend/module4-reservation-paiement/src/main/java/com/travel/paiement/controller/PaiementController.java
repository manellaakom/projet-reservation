package com.travel.paiement.controller;

import com.travel.paiement.dto.PaiementRequest;
import com.travel.paiement.dto.PaiementResponse;
import com.travel.paiement.dto.PaymeeCheckoutRequest;
import com.travel.paiement.dto.PaymeeCheckoutResponse;
import com.travel.paiement.dto.PaymeeWebhookRequest;
import com.travel.paiement.service.PaiementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @PostMapping
    public ResponseEntity<PaiementResponse> create(@Valid @RequestBody PaiementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paiementService.create(request));
    }

    @PostMapping("/paymee/checkout")
    public ResponseEntity<PaymeeCheckoutResponse> createPaymeeCheckout(
            @Valid @RequestBody PaymeeCheckoutRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paiementService.createPaymeeCheckout(request));
    }

    @GetMapping("/{id}/verify")
    public ResponseEntity<PaiementResponse> verifyPaymeePayment(@PathVariable String id) {
        return ResponseEntity.ok(paiementService.verifyPaymeePayment(id));
    }

    @PostMapping("/paymee/webhook")
    public ResponseEntity<Void> paymeeWebhook(@Valid @RequestBody PaymeeWebhookRequest request) {
        paiementService.handlePaymeeWebhook(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PaiementResponse>> findAll() {
        return ResponseEntity.ok(paiementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaiementResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(paiementService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaiementResponse> update(@PathVariable String id, @Valid @RequestBody PaiementRequest request) {
        return ResponseEntity.ok(paiementService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        paiementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}