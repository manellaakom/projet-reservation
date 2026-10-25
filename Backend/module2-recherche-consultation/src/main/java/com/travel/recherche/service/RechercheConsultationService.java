package com.travel.recherche.service;

import com.travel.recherche.dto.*;
import com.travel.recherche.entity.Voyage;
import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.TypeTransport;
import com.travel.recherche.exception.InvalidComparisonRequestException;
import com.travel.recherche.exception.ResourceNotFoundException;
import com.travel.recherche.mapper.VoyageMapper;
import com.travel.recherche.repository.VoyageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RechercheConsultationService {

    private final VoyageRepository voyageRepository;
    private final VoyageMapper voyageMapper;

    public RechercheConsultationService(
            VoyageRepository voyageRepository,
            VoyageMapper voyageMapper) {
        this.voyageRepository = voyageRepository;
        this.voyageMapper = voyageMapper;
    }

    // =========================================================================
    // 1. RECHERCHE, FILTRAGE MULTICRITÈRES ET TRI DES VOYAGES
    // =========================================================================

    public List<VoyageResponse> rechercherEtFiltrerVoyages(CritereRechercheRequest criteres) {
        List<Voyage> tousLesVoyages = voyageRepository.findAll();

        return tousLesVoyages.stream()
                .filter(v -> filtrerVoyage(v, criteres))
                .map(voyageMapper::toResponse)
                .sorted(creerComparateurTri(criteres.getSortBy(), criteres.getSortDir()))
                .collect(Collectors.toList());
    }

    private boolean filtrerVoyage(Voyage v, CritereRechercheRequest c) {
        if (c == null) return true;

        // Mot-clé (titre, description, villes, pays, compagnie, code)
        if (c.getMotCle() != null && !c.getMotCle().isBlank()) {
            String q = c.getMotCle().trim().toLowerCase();
            boolean match = (v.getTitre() != null && v.getTitre().toLowerCase().contains(q))
                    || (v.getDescription() != null && v.getDescription().toLowerCase().contains(q))
                    || (v.getVilleDepart() != null && v.getVilleDepart().toLowerCase().contains(q))
                    || (v.getVilleDestination() != null && v.getVilleDestination().toLowerCase().contains(q))
                    || (v.getPaysDestination() != null && v.getPaysDestination().toLowerCase().contains(q))
                    || (v.getCompagnie() != null && v.getCompagnie().toLowerCase().contains(q))
                    || (v.getCodeVoyage() != null && v.getCodeVoyage().toLowerCase().contains(q));
            if (!match) return false;
        }

        // Ville de départ
        if (c.getVilleDepart() != null && !c.getVilleDepart().isBlank()) {
            if (v.getVilleDepart() == null || !v.getVilleDepart().toLowerCase().contains(c.getVilleDepart().trim().toLowerCase())) {
                return false;
            }
        }

        // Ville de destination
        if (c.getVilleDestination() != null && !c.getVilleDestination().isBlank()) {
            if (v.getVilleDestination() == null || !v.getVilleDestination().toLowerCase().contains(c.getVilleDestination().trim().toLowerCase())) {
                return false;
            }
        }

        // Pays de destination
        if (c.getPaysDestination() != null && !c.getPaysDestination().isBlank()) {
            if (v.getPaysDestination() == null || !v.getPaysDestination().toLowerCase().contains(c.getPaysDestination().trim().toLowerCase())) {
                return false;
            }
        }

        // Compagnie
        if (c.getCompagnie() != null && !c.getCompagnie().isBlank()) {
            if (v.getCompagnie() == null || !v.getCompagnie().toLowerCase().contains(c.getCompagnie().trim().toLowerCase())) {
                return false;
            }
        }

        // Type de transport
        if (c.getTypeTransport() != null && v.getTypeTransport() != c.getTypeTransport()) {
            return false;
        }

        // Catégorie d'offre
        if (c.getCategorie() != null && v.getCategorie() != c.getCategorie()) {
            return false;
        }

        // Dates de départ
        if (c.getDateDepartMin() != null && v.getDateDepart() != null) {
            if (v.getDateDepart().toLocalDate().isBefore(c.getDateDepartMin())) {
                return false;
            }
        }
        if (c.getDateDepartMax() != null && v.getDateDepart() != null) {
            if (v.getDateDepart().toLocalDate().isAfter(c.getDateDepartMax())) {
                return false;
            }
        }

        // Budget (Prix effectif)
        BigDecimal prix = v.getPrixEffectif();
        if (c.getPrixMin() != null && prix != null && prix.compareTo(c.getPrixMin()) < 0) {
            return false;
        }
        if (c.getPrixMax() != null && prix != null && prix.compareTo(c.getPrixMax()) > 0) {
            return false;
        }

        // Durée maximale
        if (c.getDureeMaxMinutes() != null && v.getDureeMinutes() != null && v.getDureeMinutes() > c.getDureeMaxMinutes()) {
            return false;
        }

        // Trajet direct uniquement (0 escale)
        if (Boolean.TRUE.equals(c.getVolDirectUniquement()) && (v.getEscales() == null || v.getEscales() > 0)) {
            return false;
        }

        // En promotion uniquement
        if (Boolean.TRUE.equals(c.getEnPromotionUniquement()) && !Boolean.TRUE.equals(v.getEnPromotion())) {
            return false;
        }

        // Bagage en soute inclus
        if (Boolean.TRUE.equals(c.getBagageSouteInclus()) && !Boolean.TRUE.equals(v.getBagageSouteInclus())) {
            return false;
        }

        // Annulation gratuite
        if (Boolean.TRUE.equals(c.getAnnulationGratuite()) && !Boolean.TRUE.equals(v.getAnnulationGratuite())) {
            return false;
        }

        // Nombre minimum de places disponibles
        if (c.getPlacesMin() != null && (v.getPlacesDisponibles() == null || v.getPlacesDisponibles() < c.getPlacesMin())) {
            return false;
        }

        // Note minimale
        if (c.getNoteMin() != null && (v.getNoteMoyenne() == null || v.getNoteMoyenne() < c.getNoteMin())) {
            return false;
        }

        return true;
    }

    private Comparator<VoyageResponse> creerComparateurTri(String sortBy, String sortDir) {
        String critere = (sortBy != null) ? sortBy.trim().toLowerCase() : "prix";
        boolean desc = "desc".equalsIgnoreCase(sortDir);

        Comparator<VoyageResponse> comparator = switch (critere) {
            case "duree", "dureeminutes" -> Comparator.comparing(
                    VoyageResponse::getDureeMinutes, Comparator.nullsLast(Integer::compareTo));
            case "datedepart", "date" -> Comparator.comparing(
                    VoyageResponse::getDateDepart, Comparator.nullsLast(LocalDateTime::compareTo));
            case "note", "notemoyenne" -> Comparator.comparing(
                    VoyageResponse::getNoteMoyenne, Comparator.nullsLast(Double::compareTo));
            case "popularite", "consultations" -> Comparator.comparing(
                    VoyageResponse::getNombreConsultations, Comparator.nullsLast(Integer::compareTo));
            case "score", "qualiteprix" -> Comparator.comparing(
                    VoyageResponse::getScoreQualitePrix, Comparator.nullsLast(Integer::compareTo));
            default -> Comparator.comparing(
                    VoyageResponse::getPrixEffectif, Comparator.nullsLast(BigDecimal::compareTo));
        };

        return desc ? comparator.reversed() : comparator;
    }

    // =========================================================================
    // 2. CRUD COMPLET DE L'ENTITÉ UNIQUE VOYAGE
    // =========================================================================

    public List<VoyageResponse> listerTousLesVoyages() {
        return voyageRepository.findAll().stream()
                .map(voyageMapper::toResponse)
                .collect(Collectors.toList());
    }

    public VoyageResponse consulterVoyageParId(Long id) {
        Voyage voyage = voyageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voyage introuvable avec l'ID : " + id));
        voyage.setNombreConsultations((voyage.getNombreConsultations() != null ? voyage.getNombreConsultations() : 0) + 1);
        Voyage saved = voyageRepository.save(voyage);
        return voyageMapper.toResponse(saved);
    }

    public VoyageResponse creerVoyage(VoyageRequest request) {
        Voyage voyage = voyageMapper.toEntity(request);
        Voyage saved = voyageRepository.save(voyage);
        return voyageMapper.toResponse(saved);
    }

    public VoyageResponse modifierVoyage(Long id, VoyageRequest request) {
        Voyage existant = voyageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voyage introuvable avec l'ID : " + id));

        voyageMapper.updateEntity(existant, request);
        return voyageMapper.toResponse(voyageRepository.save(existant));
    }

    public void supprimerVoyage(Long id) {
        if (!voyageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Voyage introuvable avec l'ID : " + id);
        }
        voyageRepository.deleteById(id);
    }

    // =========================================================================
    // 3. COMPARAISON D'OFFRES DE VOYAGES
    // =========================================================================

    @Transactional(readOnly = true)
    public ComparaisonResponse comparerOffres(List<Long> ids) {
        if (ids == null || ids.size() < 2) {
            throw new InvalidComparisonRequestException("Veuillez sélectionner au moins 2 voyages à comparer.");
        }
        if (ids.size() > 5) {
            throw new InvalidComparisonRequestException("Vous pouvez comparer jusqu'à 5 voyages simultanément.");
        }

        List<VoyageResponse> offres = ids.stream()
                .distinct()
                .map(id -> voyageRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Voyage introuvable avec l'ID : " + id)))
                .map(voyageMapper::toResponse)
                .collect(Collectors.toList());

        if (offres.size() < 2) {
            throw new InvalidComparisonRequestException("Veuillez fournir au moins 2 identifiants distincts.");
        }

        VoyageResponse moinsChere = offres.stream()
                .min(Comparator.comparing(VoyageResponse::getPrixEffectif))
                .orElse(offres.get(0));

        VoyageResponse plusChere = offres.stream()
                .max(Comparator.comparing(VoyageResponse::getPrixEffectif))
                .orElse(offres.get(0));

        VoyageResponse plusRapide = offres.stream()
                .min(Comparator.comparing(VoyageResponse::getDureeMinutes))
                .orElse(offres.get(0));

        VoyageResponse plusLente = offres.stream()
                .max(Comparator.comparing(VoyageResponse::getDureeMinutes))
                .orElse(offres.get(0));

        VoyageResponse mieuxNotee = offres.stream()
                .max(Comparator.comparing(VoyageResponse::getNoteMoyenne))
                .orElse(offres.get(0));

        VoyageResponse meilleurRapport = offres.stream()
                .max(Comparator.comparing(VoyageResponse::getScoreQualitePrix))
                .orElse(offres.get(0));

        BigDecimal ecartPrix = plusChere.getPrixEffectif().subtract(moinsChere.getPrixEffectif());

        String synthese = String.format(
                "Notre algorithme recommande « %s » (%s) avec un score qualité/prix de %d/100. "
                        + "Pour le budget le plus bas, choisissez « %s » à %.2f TND (économie de %.2f TND). "
                        + "Pour le trajet le plus court, privilégiez « %s » (%s).",
                meilleurRapport.getTitre(),
                meilleurRapport.getCompagnie(),
                meilleurRapport.getScoreQualitePrix(),
                moinsChere.getTitre(),
                moinsChere.getPrixEffectif(),
                ecartPrix,
                plusRapide.getTitre(),
                plusRapide.getDureeFormatee()
        );

        int ecartDuree = plusLente.getDureeMinutes() - plusRapide.getDureeMinutes();

        return ComparaisonResponse.builder()
                .nombreOffresComparees(offres.size())
                .offreMoinsChereId(moinsChere.getId())
                .offreMoinsChereTitre(moinsChere.getTitre())
                .meilleurPrix(moinsChere.getPrixEffectif())
                .offrePlusRapideId(plusRapide.getId())
                .offrePlusRapideTitre(plusRapide.getTitre())
                .dureeMinimaleMinutes(plusRapide.getDureeMinutes())
                .offreMieuxNoteeId(mieuxNotee.getId())
                .offreMieuxNoteeTitre(mieuxNotee.getTitre())
                .meilleureNote(mieuxNotee.getNoteMoyenne())
                .offreMeilleurRapportQualitePrixId(meilleurRapport.getId())
                .offreMeilleurRapportQualitePrixTitre(meilleurRapport.getTitre())
                .meilleurScoreQualitePrix(meilleurRapport.getScoreQualitePrix())
                .ecartPrixMax(ecartPrix)
                .ecartDureeMinutes(ecartDuree)
                .syntheseRecommandation(synthese)
                .offresComparees(offres)
                .build();
    }

    // =========================================================================
    // 4. SUGGESTIONS ET RECOMMANDATIONS INTELLIGENTES
    // =========================================================================

    @Transactional(readOnly = true)
    public SuggestionsResponse obtenirSuggestionsGlobales(
            String destination,
            BigDecimal budgetMax,
            TypeTransport transport) {

        List<VoyageResponse> tous = voyageRepository.findAll().stream()
                .filter(v -> destination == null || destination.isBlank()
                        || (v.getVilleDestination() != null && v.getVilleDestination().equalsIgnoreCase(destination.trim())))
                .filter(v -> transport == null || v.getTypeTransport() == transport)
                .map(voyageMapper::toResponse)
                .collect(Collectors.toList());

        List<VoyageResponse> tendances = tous.stream()
                .sorted(Comparator.comparing(VoyageResponse::getNombreConsultations, Comparator.nullsLast(Integer::compareTo)).reversed()
                        .thenComparing(Comparator.comparing(VoyageResponse::getNoteMoyenne, Comparator.nullsLast(Double::compareTo)).reversed()))
                .limit(4)
                .collect(Collectors.toList());

        List<VoyageResponse> promos = tous.stream()
                .filter(v -> Boolean.TRUE.equals(v.getEnPromotion()))
                .sorted(Comparator.comparing(VoyageResponse::getPourcentageReduction, Comparator.nullsLast(Integer::compareTo)).reversed())
                .limit(4)
                .collect(Collectors.toList());

        List<VoyageResponse> petitsBudgets = tous.stream()
                .filter(v -> budgetMax == null || v.getPrixEffectif().compareTo(budgetMax) <= 0)
                .sorted(Comparator.comparing(VoyageResponse::getPrixEffectif))
                .limit(4)
                .collect(Collectors.toList());

        List<VoyageResponse> rapportQualitePrix = tous.stream()
                .sorted(Comparator.comparing(VoyageResponse::getScoreQualitePrix, Comparator.nullsLast(Integer::compareTo)).reversed())
                .limit(4)
                .collect(Collectors.toList());

        String ctx = "Recommandations globales du catalogue";
        if (destination != null && !destination.isBlank()) {
            ctx += " pour la destination " + destination;
        }

        return SuggestionsResponse.builder()
                .contexteSuggestion(ctx)
                .offresTendances(tendances)
                .meilleuresPromotions(promos)
                .petitsBudgets(petitsBudgets)
                .meilleurRapportQualitePrix(rapportQualitePrix)
                .build();
    }

    @Transactional(readOnly = true)
    public SuggestionsResponse obtenirSuggestionsPourOffre(Long idReference) {
        Voyage refEntity = voyageRepository.findById(idReference)
                .orElseThrow(() -> new ResourceNotFoundException("Voyage introuvable avec l'ID : " + idReference));
        VoyageResponse reference = voyageMapper.toResponse(refEntity);

        List<VoyageResponse> autresOffres = voyageRepository.findAll().stream()
                .filter(v -> !v.getId().equals(idReference))
                .map(voyageMapper::toResponse)
                .collect(Collectors.toList());

        List<VoyageResponse> similaires = autresOffres.stream()
                .filter(v -> (reference.getVilleDestination() != null && reference.getVilleDestination().equalsIgnoreCase(v.getVilleDestination()))
                        || (reference.getTypeTransport() != null && reference.getTypeTransport() == v.getTypeTransport()))
                .sorted(Comparator.comparing(VoyageResponse::getScoreQualitePrix).reversed())
                .limit(4)
                .collect(Collectors.toList());

        BigDecimal prixRef = reference.getPrixEffectif();
        List<VoyageResponse> moinsCheres = autresOffres.stream()
                .filter(v -> (reference.getVilleDestination() != null && reference.getVilleDestination().equalsIgnoreCase(v.getVilleDestination()))
                        || (reference.getPaysDestination() != null && reference.getPaysDestination().equalsIgnoreCase(v.getPaysDestination())))
                .filter(v -> v.getPrixEffectif().compareTo(prixRef) < 0)
                .sorted(Comparator.comparing(VoyageResponse::getPrixEffectif))
                .limit(4)
                .collect(Collectors.toList());

        List<VoyageResponse> topQualitePrix = autresOffres.stream()
                .sorted(Comparator.comparing(VoyageResponse::getScoreQualitePrix).reversed())
                .limit(3)
                .collect(Collectors.toList());

        return SuggestionsResponse.builder()
                .contexteSuggestion("Suggestions associées à l'offre « " + reference.getTitre() + " » (" + reference.getVilleDestination() + ")")
                .offresSimilaires(similaires)
                .alternativesMoinsCheres(moinsCheres)
                .meilleurRapportQualitePrix(topQualitePrix)
                .build();
    }

    // =========================================================================
    // 5. STATISTIQUES DU CATALOGUE DE VOYAGES
    // =========================================================================

    @Transactional(readOnly = true)
    public StatistiquesRechercheResponse getStatistiquesEtFiltres() {
        List<Voyage> voyages = voyageRepository.findAll();

        long totalPromos = voyages.stream().filter(v -> Boolean.TRUE.equals(v.getEnPromotion())).count();
        long totalConsultations = voyages.stream()
                .mapToLong(v -> v.getNombreConsultations() != null ? v.getNombreConsultations() : 0)
                .sum();

        BigDecimal prixMin = voyages.stream()
                .map(Voyage::getPrixEffectif)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal prixMax = voyages.stream()
                .map(Voyage::getPrixEffectif)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        double noteMoyenneGlobale = voyages.stream()
                .filter(v -> v.getNoteMoyenne() != null)
                .mapToDouble(Voyage::getNoteMoyenne)
                .average()
                .orElse(0.0);

        Map<String, Long> parTransport = voyages.stream()
                .collect(Collectors.groupingBy(v -> v.getTypeTransport().name(), Collectors.counting()));

        Map<String, Long> parCategorie = voyages.stream()
                .collect(Collectors.groupingBy(v -> v.getCategorie().name(), Collectors.counting()));

        return StatistiquesRechercheResponse.builder()
                .totalVoyages(voyages.size())
                .totalOffresBillets(0)
                .totalOffresEnPromotion(totalPromos)
                .totalRecherchesEffectuees(0)
                .totalConsultations(totalConsultations)
                .prixMinimumCatalogue(prixMin)
                .prixMaximumCatalogue(prixMax)
                .noteMoyenneGlobale(Math.round(noteMoyenneGlobale * 100.0) / 100.0)
                .villesDepartDisponibles(voyageRepository.findDistinctVillesDepart())
                .villesDestinationDisponibles(voyageRepository.findDistinctVillesDestination())
                .paysDestinationDisponibles(voyageRepository.findDistinctPaysDestination())
                .compagniesDisponibles(voyageRepository.findDistinctCompagnies())
                .repartitionParTypeTransport(parTransport)
                .repartitionParCategorie(parCategorie)
                .build();
    }

    // =========================================================================
    // 6. COMPATIBILITÉ INTER-SERVICES (MODULE 3 RÉSERVATION)
    // =========================================================================

    @Transactional(readOnly = true)
    public Map<String, Object> verifierDisponibilitePourReservation(Long idVoyage) {
        Voyage v = voyageRepository.findById(idVoyage)
                .orElseThrow(() -> new ResourceNotFoundException("Voyage introuvable avec l'ID : " + idVoyage));

        int placesDispo = v.getPlacesDisponibles() != null ? v.getPlacesDisponibles() : 0;
        String statutDisponibilite = placesDispo > 0 ? "DISPONIBLE" : "COMPLETE";

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("id_voyage", v.getId());
        res.put("code_voyage", v.getCodeVoyage());
        res.put("titre", v.getTitre());
        res.put("nombre_places", Math.max(placesDispo, 30));
        res.put("places_disponibles", placesDispo);
        res.put("statut", statutDisponibilite);
        res.put("prix_unitaire", v.getPrixEffectif());
        res.put("max_bagage_autorise_kg", 30.0);
        return res;
    }

    public Map<String, Object> mettreAJourPlacesDisponibles(Long idVoyage, int placesReservees) {
        Voyage v = voyageRepository.findById(idVoyage)
                .orElseThrow(() -> new ResourceNotFoundException("Voyage introuvable avec l'ID : " + idVoyage));

        int restantes = Math.max(0, (v.getPlacesDisponibles() != null ? v.getPlacesDisponibles() : 0) - placesReservees);
        v.setPlacesDisponibles(restantes);
        voyageRepository.save(v);

        return verifierDisponibilitePourReservation(idVoyage);
    }
}
