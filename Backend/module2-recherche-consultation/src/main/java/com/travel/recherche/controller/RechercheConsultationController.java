package com.travel.recherche.controller;

import com.travel.recherche.dto.*;
import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.ClasseBillet;
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
@Tag(name = "Module 2 — Recherche & Consultation", description = "Recherche de voyages et billets, filtrage, tri, consultation, comparaison d'offres et suggestions intelligentes")
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
                "service", "module2-recherche-consultation"
        ));
    }

    // =========================================================================
    // 1. RECHERCHE, FILTRAGE ET TRI DES VOYAGES
    // =========================================================================

    @GetMapping("/voyages")
    @Operation(summary = "Rechercher, filtrer et trier les offres de voyages (GET multicritères)")
    public ResponseEntity<List<VoyageResponse>> rechercherVoyages(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) String villeDepart,
            @RequestParam(required = false) String villeDestination,
            @RequestParam(required = false) String paysDestination,
            @RequestParam(required = false) String compagnie,
            @RequestParam(required = false) TypeTransport typeTransport,
            @RequestParam(required = false) CategorieOffre categorie,
            @RequestParam(required = false) ClasseBillet classeBillet,
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
                .classeBillet(classeBillet)
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

    @PostMapping("/voyages/search")
    @Operation(summary = "Rechercher, filtrer et trier les offres de voyages via un corps JSON")
    public ResponseEntity<List<VoyageResponse>> rechercherVoyagesPost(@RequestBody CritereRechercheRequest criteres) {
        return ResponseEntity.ok(service.rechercherEtFiltrerVoyages(criteres));
    }

    // =========================================================================
    // 2. RECHERCHE, FILTRAGE ET TRI DES OFFRES DE BILLETS
    // =========================================================================

    @GetMapping("/billets")
    @Operation(summary = "Rechercher, filtrer et trier directement les offres de billets par classe, budget et options")
    public ResponseEntity<List<OffreBilletDto>> rechercherBillets(
            @RequestParam(required = false) ClasseBillet classe,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(required = false) Boolean remboursable,
            @RequestParam(required = false) Boolean modifiable,
            @RequestParam(required = false) Integer bagageSouteMinKg,
            @RequestParam(required = false) String villeDepart,
            @RequestParam(required = false) String villeDestination,
            @RequestParam(defaultValue = "prix") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        return ResponseEntity.ok(service.rechercherBillets(
                classe, prixMax, remboursable, modifiable, bagageSouteMinKg,
                villeDepart, villeDestination, sortBy, sortDir));
    }

    @GetMapping("/billets/{id}")
    @Operation(summary = "Consulter le détail d'une offre de billet spécifique")
    public ResponseEntity<OffreBilletDto> consulterBillet(@PathVariable Long id) {
        return ResponseEntity.ok(service.consulterBilletParId(id));
    }

    @PostMapping("/voyages/{voyageId}/billets")
    @Operation(summary = "Ajouter une nouvelle offre de billet à un voyage existant")
    public ResponseEntity<OffreBilletDto> creerBillet(
            @PathVariable Long voyageId,
            @Valid @RequestBody OffreBilletDto request) {
        return new ResponseEntity<>(service.creerBilletPourVoyage(voyageId, request), HttpStatus.CREATED);
    }

    @PutMapping("/billets/{id}")
    @Operation(summary = "Modifier une offre de billet existante")
    public ResponseEntity<OffreBilletDto> modifierBillet(
            @PathVariable Long id,
            @Valid @RequestBody OffreBilletDto request) {
        return ResponseEntity.ok(service.modifierBillet(id, request));
    }

    @DeleteMapping("/billets/{id}")
    @Operation(summary = "Supprimer une offre de billet")
    public ResponseEntity<Void> supprimerBillet(@PathVariable Long id) {
        service.supprimerBillet(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // 3. CONSULTATION DÉTAILLÉE ET GESTION DES OFFRES DE VOYAGES
    // =========================================================================

    @GetMapping("/voyages/{id}")
    @Operation(summary = "Consulter les détails complets d'une offre de voyage (incrémente le compteur de popularité)")
    public ResponseEntity<VoyageResponse> consulterVoyage(@PathVariable Long id) {
        return ResponseEntity.ok(service.consulterVoyageParId(id));
    }

    @PostMapping("/voyages")
    @Operation(summary = "Créer une nouvelle offre de voyage avec ses classes de billets")
    public ResponseEntity<VoyageResponse> creerVoyage(@Valid @RequestBody VoyageRequest request) {
        return new ResponseEntity<>(service.creerVoyage(request), HttpStatus.CREATED);
    }

    @PutMapping("/voyages/{id}")
    @Operation(summary = "Modifier une offre de voyage existante")
    public ResponseEntity<VoyageResponse> modifierVoyage(
            @PathVariable Long id,
            @Valid @RequestBody VoyageRequest request) {
        return ResponseEntity.ok(service.modifierVoyage(id, request));
    }

    @DeleteMapping("/voyages/{id}")
    @Operation(summary = "Supprimer une offre de voyage")
    public ResponseEntity<Void> supprimerVoyage(@PathVariable Long id) {
        service.supprimerVoyage(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // 4. COMPARATEUR D'OFFRES CÔTE-À-CÔTE
    // =========================================================================

    @GetMapping("/comparer")
    @Operation(summary = "Comparer entre 2 et 5 offres côte-à-côte par leurs IDs (ex: ?ids=1,2,3)")
    public ResponseEntity<ComparaisonResponse> comparerOffresGet(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(service.comparerOffres(ids));
    }

    @PostMapping("/comparer")
    @Operation(summary = "Comparer entre 2 et 5 offres côte-à-côte via une liste d'IDs JSON")
    public ResponseEntity<ComparaisonResponse> comparerOffresPost(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(service.comparerOffres(ids));
    }

    // =========================================================================
    // 5. SUGGESTIONS ET RECOMMANDATIONS INTELLIGENTES
    // =========================================================================

    @GetMapping("/suggestions")
    @Operation(summary = "Obtenir des suggestions intelligentes (tendances, promotions, petits budgets, meilleur rapport qualité/prix)")
    public ResponseEntity<SuggestionsResponse> obtenirSuggestionsGlobales(
            @RequestParam(required = false) String villeDestination,
            @RequestParam(required = false) BigDecimal budgetMax,
            @RequestParam(required = false) TypeTransport typeTransport) {
        return ResponseEntity.ok(service.obtenirSuggestionsGlobales(villeDestination, budgetMax, typeTransport));
    }

    @GetMapping("/voyages/{id}/suggestions")
    @Operation(summary = "Obtenir des suggestions similaires et des alternatives moins chères pour une offre consultée")
    public ResponseEntity<SuggestionsResponse> obtenirSuggestionsPourOffre(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenirSuggestionsPourOffre(id));
    }

    // =========================================================================
    // 6. STATISTIQUES ET FILTRES DISPONIBLES
    // =========================================================================

    @GetMapping("/statistiques")
    @Operation(summary = "Obtenir les statistiques du catalogue et les listes de filtres disponibles (villes, compagnies, prix min/max)")
    public ResponseEntity<StatistiquesRechercheResponse> getStatistiques() {
        return ResponseEntity.ok(service.getStatistiquesEtFiltres());
    }

    // =========================================================================
    // 7. ENDPOINTS D'INTÉGRATION INTER-SERVICES (MODULE 3 RÉSERVATION)
    // =========================================================================

    @GetMapping("/voyages/{id}/disponibilite")
    @Operation(summary = "Vérifier la disponibilité et le tarif unitaire d'un voyage (format compatible Module 3 Reservation/Disponibilite)")
    public ResponseEntity<Map<String, Object>> verifierDisponibilite(@PathVariable Long id) {
        return ResponseEntity.ok(service.verifierDisponibilitePourReservation(id));
    }

    @PatchMapping("/voyages/{id}/places")
    @Operation(summary = "Décrémenter ou ajuster les places disponibles après confirmation d'une réservation dans le Module 3")
    public ResponseEntity<Map<String, Object>> mettreAJourPlaces(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int placesReservees) {
        return ResponseEntity.ok(service.mettreAJourPlacesDisponibles(id, placesReservees));
    }
}
