package com.travel.bagage.controller;

import com.travel.bagage.client.PaymentDto;
import com.travel.bagage.client.ReservationDto;
import com.travel.bagage.dto.BagageRequest;
import com.travel.bagage.dto.BagageResponse;
import com.travel.bagage.dto.BagageStatisticsResponse;
import com.travel.bagage.messaging.BaggageProducer;
import com.travel.bagage.service.BagageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bagages")
@Tag(name = "Gestion des Bagages", description = "Endpoints CRUD, statistiques, tarification dynamique, Feign & RabbitMQ")
public class BagageController {

    private final BagageService bagageService;

    @Autowired(required = false)
    private BaggageProducer baggageProducer;

    public BagageController(BagageService bagageService) {
        this.bagageService = bagageService;
    }

    @PostMapping
    @Operation(summary = "Créer un bagage", description = "Enregistre un bagage avec tarification automatique (30/50/70 DT), validation Feign et émission RabbitMQ")
    public ResponseEntity<BagageResponse> createBagage(@Valid @RequestBody BagageRequest request) {
        return new ResponseEntity<>(bagageService.createBagage(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lister tous les bagages", description = "Retourne la liste complète des bagages stockés dans Redis")
    public ResponseEntity<List<BagageResponse>> getAllBagages() {
        return ResponseEntity.ok(bagageService.getAllBagages());
    }

    @GetMapping("/statistics")
    @Operation(summary = "Obtenir les statistiques", description = "Fournit le total bagages, poids cumulé, revenu estimé et moyennes")
    public ResponseEntity<BagageStatisticsResponse> getStatistics() {
        return ResponseEntity.ok(bagageService.getStatistics());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un bagage par ID", description = "Recherche un bagage spécifique par son identifiant unique")
    public ResponseEntity<BagageResponse> getBagageById(@Parameter(description = "ID unique du bagage") @PathVariable String id) {
        return ResponseEntity.ok(bagageService.getBagageById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un bagage", description = "Met à jour un bagage existant, recalcule le prix et notifie RabbitMQ")
    public ResponseEntity<BagageResponse> updateBagage(@PathVariable String id, @Valid @RequestBody BagageRequest request) {
        return ResponseEntity.ok(bagageService.updateBagage(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un bagage", description = "Supprime un bagage de la base Redis")
    public ResponseEntity<Void> deleteBagage(@PathVariable String id) {
        bagageService.deleteBagage(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reservation/{reservationId}")
    @Operation(summary = "Associer à une réservation", description = "Lie un bagage à une référence de réservation passager")
    public ResponseEntity<BagageResponse> associateToReservation(@PathVariable String id, @PathVariable String reservationId) {
        return ResponseEntity.ok(bagageService.associateToReservation(id, reservationId));
    }

    // --- Endpoints de démonstration Feign & RabbitMQ ---

    @GetMapping("/feign/reservation/{reservationId}")
    @Operation(summary = "Test Feign 1 : Vérification Réservation", description = "Appelle le Module 3 (Réservation) via Feign Client avec Fallback Resilience4j")
    public ResponseEntity<ReservationDto> testFeignReservation(@PathVariable String reservationId) {
        return ResponseEntity.ok(bagageService.checkReservation(reservationId));
    }

    @PostMapping("/{id}/feign/notify-payment")
    @Operation(summary = "Test Feign 2 : Notification Paiement", description = "Transmet le montant calculé du bagage au Module 4 (Paiement) via Feign Client")
    public ResponseEntity<PaymentDto> testFeignPayment(@PathVariable String id) {
        return ResponseEntity.ok(bagageService.notifyPayment(id));
    }

    @PostMapping("/rabbitmq/test-event")
    @Operation(summary = "Test RabbitMQ : Publication d'un événement", description = "Publie un événement de test sur le topic exchange bagage.exchange")
    public ResponseEntity<Map<String, Object>> testRabbitMqEvent(@RequestParam(defaultValue = "TEST-BAG-001") String bagageId) {
        if (baggageProducer != null) {
            baggageProducer.sendBaggageCreatedEvent(bagageId, "RES-TEST-DEMO", 15.5, 50.0, "TEST_SENT");
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "SUCCESS");
        resp.put("message", "Événement de test émis sur RabbitMQ exchange 'bagage.exchange'");
        resp.put("bagageId", bagageId);
        return ResponseEntity.ok(resp);
    }
}
