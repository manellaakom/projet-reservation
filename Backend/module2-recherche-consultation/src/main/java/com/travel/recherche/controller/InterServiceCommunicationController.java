package com.travel.recherche.controller;

import com.travel.recherche.event.PaiementEvent;
import com.travel.recherche.event.PromotionEvent;
import com.travel.recherche.event.ReservationEvent;
import com.travel.recherche.event.TravelEventProducer;
import com.travel.recherche.service.InterServiceCommunicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Contrôleur dédié à la démonstration des communications inter-microservices :
 * - Synchrone (OpenFeign) : 3 scénarios
 * - Asynchrone (RabbitMQ) : 3 scénarios
 * Répond directement aux exigences de la Note 4 de la grille AWD.
 */
@RestController
@RequestMapping("/api/recherche/inter-services")
@Tag(name = "Communications Inter-Microservices (Feign & RabbitMQ)",
     description = "Démonstrations des communications synchrones OpenFeign et asynchrones RabbitMQ (Note 4)")
public class InterServiceCommunicationController {

    private final InterServiceCommunicationService feignService;
    private final TravelEventProducer eventProducer;

    public InterServiceCommunicationController(InterServiceCommunicationService feignService,
                                                TravelEventProducer eventProducer) {
        this.feignService = feignService;
        this.eventProducer = eventProducer;
    }

    // =========================================================================
    // COMMUNICATIONS SYNCHRONES (OPENFEIGN)
    // =========================================================================

    @GetMapping("/feign/disponibilite/{voyageId}")
    @Operation(summary = "Feign 1 : Vérifier la disponibilité en temps réel (Appel Module 3 FastAPI)")
    public ResponseEntity<Map<String, Object>> verifierDisponibilite(@PathVariable Long voyageId) {
        return ResponseEntity.ok(feignService.verifierDisponibiliteVoyage(voyageId));
    }

    @GetMapping("/feign/bagages")
    @Operation(summary = "Feign 2 : Consulter les options et statistiques bagages (Appel Module 1 Spring Boot)")
    public ResponseEntity<Map<String, Object>> consulterBagages() {
        return ResponseEntity.ok(feignService.consulterBagagesEtStats());
    }

    @GetMapping("/feign/paiements")
    @Operation(summary = "Feign 3 : Consulter l'état des paiements (Appel Module 4 Spring Boot)")
    public ResponseEntity<Map<String, Object>> consulterPaiements() {
        return ResponseEntity.ok(feignService.consulterPaiements());
    }

    @GetMapping("/feign/synthese/{voyageId}")
    @Operation(summary = "Feign Synthèse : Agrégation multi-microservices pour une offre de voyage")
    public ResponseEntity<Map<String, Object>> syntheseComplete(@PathVariable Long voyageId) {
        return ResponseEntity.ok(feignService.syntheseCompleteVoyage(voyageId));
    }

    // =========================================================================
    // COMMUNICATIONS ASYNCHRONES (RABBITMQ)
    // =========================================================================

    @PostMapping("/rabbitmq/publier-reservation")
    @Operation(summary = "RabbitMQ 1 : Publier un événement de réservation (Décrémente les places et booste la popularité)")
    public ResponseEntity<Map<String, Object>> simulerReservation(
            @RequestParam(defaultValue = "1") Long voyageId,
            @RequestParam(defaultValue = "2") int nombrePlaces,
            @RequestParam(defaultValue = "client@esprit.tn") String emailClient) {

        ReservationEvent event = new ReservationEvent(
                voyageId,
                nombrePlaces,
                emailClient,
                LocalDateTime.now()
        );
        eventProducer.envoyerReservationEvent(event);

        return ResponseEntity.ok(Map.of(
                "status", "PUBLIE",
                "exchange", "travel.direct.exchange",
                "routingKey", "reservation.created.key",
                "message", "Événement de réservation envoyé dans RabbitMQ. Le consommateur met à jour le voyage #" + voyageId,
                "event", event
        ));
    }

    @PostMapping("/rabbitmq/publier-paiement")
    @Operation(summary = "RabbitMQ 2 : Publier un événement de confirmation de paiement (Validation asynchrone)")
    public ResponseEntity<Map<String, Object>> simulerPaiement(
            @RequestParam(defaultValue = "1") Long voyageId,
            @RequestParam(defaultValue = "250.00") BigDecimal montant,
            @RequestParam(defaultValue = "CARTE_BANCAIRE") String modePaiement) {

        PaiementEvent event = new PaiementEvent(
                "PAY-" + System.currentTimeMillis(),
                voyageId,
                montant,
                modePaiement,
                "VALIDE",
                LocalDateTime.now()
        );
        eventProducer.envoyerPaiementEvent(event);

        return ResponseEntity.ok(Map.of(
                "status", "PUBLIE",
                "exchange", "travel.direct.exchange",
                "routingKey", "paiement.success.key",
                "message", "Paiement validé publié dans RabbitMQ.",
                "event", event
        ));
    }

    @PostMapping("/rabbitmq/publier-promotion")
    @Operation(summary = "RabbitMQ 3 : Publier une promotion flash (Topic exchange broadcast)")
    public ResponseEntity<Map<String, Object>> simulerPromotion(
            @RequestParam(defaultValue = "1") Long voyageId,
            @RequestParam(defaultValue = "Vente Flash Tunis-Paris") String titre,
            @RequestParam(defaultValue = "450.00") BigDecimal ancienPrix,
            @RequestParam(defaultValue = "350.00") BigDecimal nouveauPrix,
            @RequestParam(defaultValue = "25.0") Double pourcentageRemise) {

        PromotionEvent event = new PromotionEvent(
                voyageId,
                titre,
                ancienPrix,
                nouveauPrix,
                pourcentageRemise,
                LocalDateTime.now().plusDays(2)
        );
        eventProducer.envoyerPromotionEvent(event);

        return ResponseEntity.ok(Map.of(
                "status", "PUBLIE",
                "exchange", "travel.topic.exchange",
                "routingKey", "voyage.promo.flash",
                "message", "Promotion flash diffusée via RabbitMQ Topic.",
                "event", event
        ));
    }
}
