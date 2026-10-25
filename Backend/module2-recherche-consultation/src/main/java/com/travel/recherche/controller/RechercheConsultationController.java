package com.travel.recherche.controller;

import com.travel.recherche.dto.*;
import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.TypeTransport;
import com.travel.recherche.service.RechercheConsultationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recherche")
@Tag(name = "Module 2 — Gestion & Recherche des Voyages", description = "Microservice de gestion unique de l'entité Voyage : CRUD complet, recherche multi-critères, filtres, tri et suggestions")
public class RechercheConsultationController {

    private final RechercheConsultationService service;

    public RechercheConsultationController(RechercheConsultationService service) {
        this.service = service;
    }

    @GetMapping("/health")
    @Operation(summary = "Vérifier l'état de santé du microservice")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "module2-recherche-consultation",
                "entite", "Voyage"
        ));
    }

    // =========================================================================
    // 1. CRUD COMPLET DE L'ENTITÉ VOYAGE
    // =========================================================================

    @PostMapping("/voyages")
    @Operation(summary = "1. [CREATE] Créer une nouvelle offre de voyage")
    public ResponseEntity<VoyageResponse> creerVoyage(@Valid @RequestBody VoyageRequest request) {
        return new ResponseEntity<>(service.creerVoyage(request), HttpStatus.CREATED);
    }

    @GetMapping("/voyages")
    @Operation(summary = "2. [READ ALL / SEARCH] Rechercher, filtrer et trier les voyages")
    public ResponseEntity<List<VoyageResponse>> rechercherVoyages(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) String villeDepart,
            @RequestParam(required = false) String villeDestination,
            @RequestParam(required = false) String paysDestination,
            @RequestParam(required = false) String compagnie,
            @RequestParam(required = false) TypeTransport typeTransport,
            @RequestParam(required = false) CategorieOffre categorie,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDepartMin,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDepartMax,
            @RequestParam(required = false) BigDecimal prixMin,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(required = false) Integer dureeMaxMinutes,
            @RequestParam(required = false) Boolean volDirectUniquement,
            @RequestParam(required = false) Boolean enPromotionUniquement,
            @RequestParam(required = false) Boolean bagageSouteInclus,
            @RequestParam(required = false) Boolean annulationGratuite,
            @RequestParam(required = false) Integer placesMin,
            @RequestParam(required = false) Double noteMin,
            @RequestParam(defaultValue = "prix") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        CritereRechercheRequest criteres = CritereRechercheRequest.builder()
                .motCle(motCle)
                .villeDepart(villeDepart)
                .villeDestination(villeDestination)
                .paysDestination(paysDestination)
                .compagnie(compagnie)
                .typeTransport(typeTransport)
                .categorie(categorie)
                .dateDepartMin(dateDepartMin)
                .dateDepartMax(dateDepartMax)
                .prixMin(prixMin)
                .prixMax(prixMax)
                .dureeMaxMinutes(dureeMaxMinutes)
                .volDirectUniquement(volDirectUniquement)
                .enPromotionUniquement(enPromotionUniquement)
                .bagageSouteInclus(bagageSouteInclus)
                .annulationGratuite(annulationGratuite)
                .placesMin(placesMin)
                .noteMin(noteMin)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        return ResponseEntity.ok(service.rechercherEtFiltrerVoyages(criteres));
    }

    @GetMapping("/voyages/{id}")
    @Operation(summary = "3. [READ ONE] Consulter les détails d'un voyage par son ID")
    public ResponseEntity<VoyageResponse> consulterVoyage(@PathVariable Long id) {
        return ResponseEntity.ok(service.consulterVoyageParId(id));
    }

    @PutMapping("/voyages/{id}")
    @Operation(summary = "4. [UPDATE] Modifier une offre de voyage existante")
    public ResponseEntity<VoyageResponse> modifierVoyage(
            @PathVariable Long id,
            @Valid @RequestBody VoyageRequest request) {
        return ResponseEntity.ok(service.modifierVoyage(id, request));
    }

    @DeleteMapping("/voyages/{id}")
    @Operation(summary = "5. [DELETE] Supprimer une offre de voyage")
    public ResponseEntity<Void> supprimerVoyage(@PathVariable Long id) {
        service.supprimerVoyage(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // 2. RECHERCHE AVANCÉE, COMPARAISON ET SUGGESTIONS
    // =========================================================================

    @PostMapping("/voyages/search")
    @Operation(summary = "Rechercher et filtrer les voyages via un corps JSON")
    public ResponseEntity<List<VoyageResponse>> rechercherVoyagesPost(@RequestBody CritereRechercheRequest criteres) {
        return ResponseEntity.ok(service.rechercherEtFiltrerVoyages(criteres));
    }

    @GetMapping("/comparer")
    @Operation(summary = "Comparer entre 2 et 5 offres de voyage côte-à-côte (ex: ?ids=1,2)")
    public ResponseEntity<ComparaisonResponse> comparerOffresGet(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(service.comparerOffres(ids));
    }

    @PostMapping("/comparer")
    @Operation(summary = "Comparer des voyages via une liste d'IDs JSON")
    public ResponseEntity<ComparaisonResponse> comparerOffresPost(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(service.comparerOffres(ids));
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Obtenir des suggestions intelligentes (tendances, promotions, petits budgets)")
    public ResponseEntity<SuggestionsResponse> obtenirSuggestionsGlobales(
            @RequestParam(required = false) String villeDestination,
            @RequestParam(required = false) BigDecimal budgetMax,
            @RequestParam(required = false) TypeTransport typeTransport) {
        return ResponseEntity.ok(service.obtenirSuggestionsGlobales(villeDestination, budgetMax, typeTransport));
    }

    @GetMapping("/voyages/{id}/suggestions")
    @Operation(summary = "Obtenir des alternatives et suggestions similaires pour un voyage")
    public ResponseEntity<SuggestionsResponse> obtenirSuggestionsPourOffre(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenirSuggestionsPourOffre(id));
    }

    @GetMapping("/statistiques")
    @Operation(summary = "Obtenir les statistiques du catalogue et les filtres disponibles")
    public ResponseEntity<StatistiquesRechercheResponse> getStatistiques() {
        return ResponseEntity.ok(service.getStatistiquesEtFiltres());
    }

    // =========================================================================
    // 3. INTÉGRATION INTER-SERVICES (MODULE 3 RÉSERVATION)
    // =========================================================================

    @GetMapping("/voyages/{id}/disponibilite")
    @Operation(summary = "Vérifier la disponibilité et le tarif d'un voyage")
    public ResponseEntity<Map<String, Object>> verifierDisponibilite(@PathVariable Long id) {
        return ResponseEntity.ok(service.verifierDisponibilitePourReservation(id));
    }

    @PatchMapping("/voyages/{id}/places")
    @Operation(summary = "Mettre à jour les places disponibles après une réservation")
    public ResponseEntity<Map<String, Object>> mettreAJourPlaces(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int placesReservees) {
        return ResponseEntity.ok(service.mettreAJourPlacesDisponibles(id, placesReservees));
    }
}
