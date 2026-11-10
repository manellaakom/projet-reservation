package com.travel.recherche.service;

import com.travel.recherche.dto.*;
import com.travel.recherche.entity.HistoriqueRecherche;
import com.travel.recherche.entity.OffreBillet;
import com.travel.recherche.entity.Voyage;
import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.ClasseBillet;
import com.travel.recherche.enums.TypeTransport;
import com.travel.recherche.exception.InvalidComparisonRequestException;
import com.travel.recherche.exception.ResourceNotFoundException;
import com.travel.recherche.mapper.VoyageMapper;
import com.travel.recherche.repository.HistoriqueRechercheRepository;
import com.travel.recherche.repository.OffreBilletRepository;
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
    private final OffreBilletRepository offreBilletRepository;
    private final HistoriqueRechercheRepository historiqueRepository;
    private final VoyageMapper voyageMapper;

    public RechercheConsultationService(
            VoyageRepository voyageRepository,
            OffreBilletRepository offreBilletRepository,
            HistoriqueRechercheRepository historiqueRepository,
            VoyageMapper voyageMapper) {
        this.voyageRepository = voyageRepository;
        this.offreBilletRepository = offreBilletRepository;
        this.historiqueRepository = historiqueRepository;
        this.voyageMapper = voyageMapper;
    }

    // =========================================================================
    // 1. RECHERCHE, FILTRAGE MULTICRITÈRES ET TRI DES VOYAGES & OFFRES
    // =========================================================================

    public List<VoyageResponse> rechercherEtFiltrerVoyages(CritereRechercheRequest criteres) {
        List<Voyage> tousLesVoyages = voyageRepository.findAll();

        List<VoyageResponse> resultats = tousLesVoyages.stream()
                .filter(v -> filtrerVoyage(v, criteres))
                .map(voyageMapper::toResponse)
                .sorted(creerComparateurTri(criteres.getSortBy(), criteres.getSortDir()))
                .collect(Collectors.toList());

        // Enregistrer la recherche dans l'historique si au moins un critère significatif est fourni
        if (possedeCritereActif(criteres)) {
            historiqueRepository.save(HistoriqueRecherche.builder()
                    .motCle(criteres.getMotCle())
                    .villeDepart(criteres.getVilleDepart())
                    .villeDestination(criteres.getVilleDestination())
                    .typeTransport(criteres.getTypeTransport())
                    .classeBillet(criteres.getClasseBillet())
                    .budgetMax(criteres.getPrixMax())
                    .nombreResultats(resultats.size())
                    .dateRecherche(LocalDateTime.now())
                    .build());
        }

        return resultats;
    }

    private boolean filtrerVoyage(Voyage v, CritereRechercheRequest c) {
        if (c == null) return true;

        // Mot-clé (titre, description, villeDestination, pays, compagnie, code)
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

        // Classe de billet disponible dans l'offre
        if (c.getClasseBillet() != null) {
            boolean hasClass = v.getBillets() != null && v.getBillets().stream()
                    .anyMatch(b -> b.getClasse() == c.getClasseBillet() && b.getPlacesRestantes() > 0);
            if (!hasClass) return false;
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

    private boolean possedeCritereActif(CritereRechercheRequest c) {
        if (c == null) return false;
        return (c.getMotCle() != null && !c.getMotCle().isBlank())
                || (c.getVilleDepart() != null && !c.getVilleDepart().isBlank())
                || (c.getVilleDestination() != null && !c.getVilleDestination().isBlank())
                || c.getTypeTransport() != null
                || c.getCategorie() != null
                || c.getClasseBillet() != null
                || c.getPrixMax() != null;
    }

    // =========================================================================
    // 2. RECHERCHE ET FILTRAGE DES OFFRES DE BILLETS
    // =========================================================================

    @Transactional(readOnly = true)
    public List<OffreBilletDto> rechercherBillets(
            ClasseBillet classe,
            BigDecimal prixMax,
            Boolean remboursable,
            Boolean modifiable,
            Integer bagageSouteMinKg,
            String villeDepart,
            String villeDestination,
            String sortBy,
            String sortDir) {

        List<OffreBillet> billets = offreBilletRepository.findAll();

        Comparator<OffreBilletDto> comparator = switch (sortBy != null ? sortBy.toLowerCase() : "prix") {
            case "bagage", "bagagesoutekg" -> Comparator.comparing(
                    OffreBilletDto::getBagageSouteKg, Comparator.nullsLast(Integer::compareTo));
            case "places", "placesrestantes" -> Comparator.comparing(
                    OffreBilletDto::getPlacesRestantes, Comparator.nullsLast(Integer::compareTo));
            default -> Comparator.comparing(
                    OffreBilletDto::getPrix, Comparator.nullsLast(BigDecimal::compareTo));
        };

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        return billets.stream()
                .filter(b -> classe == null || b.getClasse() == classe)
                .filter(b -> prixMax == null || (b.getPrix() != null && b.getPrix().compareTo(prixMax) <= 0))
                .filter(b -> !Boolean.TRUE.equals(remboursable) || Boolean.TRUE.equals(b.getRemboursable()))
                .filter(b -> !Boolean.TRUE.equals(modifiable) || Boolean.TRUE.equals(b.getModifiable()))
                .filter(b -> bagageSouteMinKg == null || (b.getBagageSouteKg() != null && b.getBagageSouteKg() >= bagageSouteMinKg))
                .filter(b -> villeDepart == null || villeDepart.isBlank()
                        || (b.getVoyage() != null && b.getVoyage().getVilleDepart().toLowerCase().contains(villeDepart.trim().toLowerCase())))
                .filter(b -> villeDestination == null || villeDestination.isBlank()
                        || (b.getVoyage() != null && b.getVoyage().getVilleDestination().toLowerCase().contains(villeDestination.trim().toLowerCase())))
                .map(voyageMapper::toBilletDto)
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OffreBilletDto consulterBilletParId(Long id) {
        OffreBillet billet = offreBilletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre de billet introuvable avec l'ID : " + id));
        return voyageMapper.toBilletDto(billet);
    }

    public OffreBilletDto creerBilletPourVoyage(Long voyageId, OffreBilletDto dto) {
        Voyage voyage = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new ResourceNotFoundException("Offre de voyage introuvable avec l'ID : " + voyageId));
        OffreBillet billet = voyageMapper.toBilletEntity(dto);
        billet.setVoyage(voyage);
        OffreBillet saved = offreBilletRepository.save(billet);
        return voyageMapper.toBilletDto(saved);
    }

    public OffreBilletDto modifierBillet(Long id, OffreBilletDto dto) {
        OffreBillet existant = offreBilletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre de billet introuvable avec l'ID : " + id));
        existant.setCodeOffreBillet(dto.getCodeOffreBillet());
        existant.setClasse(dto.getClasse());
        existant.setPrix(dto.getPrix());
        existant.setPlacesRestantes(dto.getPlacesRestantes());
        existant.setBagageCabineKg(dto.getBagageCabineKg() != null ? dto.getBagageCabineKg() : 8);
        existant.setBagageSouteKg(dto.getBagageSouteKg() != null ? dto.getBagageSouteKg() : 0);
        existant.setModifiable(Boolean.TRUE.equals(dto.getModifiable()));
        existant.setRemboursable(Boolean.TRUE.equals(dto.getRemboursable()));
        existant.setChoixSiegeGratuit(Boolean.TRUE.equals(dto.getChoixSiegeGratuit()));
        existant.setAccesSalonVip(Boolean.TRUE.equals(dto.getAccesSalonVip()));
        existant.setConditionsTarifaires(dto.getConditionsTarifaires());
        return voyageMapper.toBilletDto(offreBilletRepository.save(existant));
    }

    public void supprimerBillet(Long id) {
        if (!offreBilletRepository.existsById(id)) {
            throw new ResourceNotFoundException("Offre de billet introuvable avec l'ID : " + id);
        }
        offreBilletRepository.deleteById(id);
    }

    // =========================================================================
    // 3. CONSULTATION DÉTAILLÉE ET GESTION DES OFFRES (CRUD)
    // =========================================================================

    public VoyageResponse consulterVoyageParId(Long id) {
        Voyage voyage = voyageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offre de voyage introuvable avec l'ID : " + id));
        // Incrémenter le compteur de consultations pour alimenter les tendances / suggestions
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
                .orElseThrow(() -> new ResourceNotFoundException("Offre de voyage introuvable avec l'ID : " + id));

        existant.setCodeVoyage(request.getCodeVoyage());
        existant.setTitre(request.getTitre());
        existant.setDescription(request.getDescription());
        existant.setVilleDepart(request.getVilleDepart());
        existant.setVilleDestination(request.getVilleDestination());
        existant.setPaysDestination(request.getPaysDestination());
        existant.setCompagnie(request.getCompagnie());
        existant.setTypeTransport(request.getTypeTransport());
        existant.setCategorie(request.getCategorie());
        existant.setDateDepart(request.getDateDepart());
        existant.setDateArrivee(request.getDateArrivee());
        existant.setDureeMinutes(request.getDureeMinutes());
        existant.setEscales(request.getEscales() != null ? request.getEscales() : 0);
        existant.setPrixBase(request.getPrixBase());
        existant.setPrixPromo(request.getPrixPromo());
        existant.setEnPromotion(Boolean.TRUE.equals(request.getEnPromotion()));
        existant.setPlacesDisponibles(request.getPlacesDisponibles());
        if (request.getNoteMoyenne() != null) existant.setNoteMoyenne(request.getNoteMoyenne());
        if (request.getNombreAvis() != null) existant.setNombreAvis(request.getNombreAvis());
        existant.setBagageCabineInclus(Boolean.TRUE.equals(request.getBagageCabineInclus()));
        existant.setBagageSouteInclus(Boolean.TRUE.equals(request.getBagageSouteInclus()));
        existant.setAnnulationGratuite(Boolean.TRUE.equals(request.getAnnulationGratuite()));
        existant.setRepasInclus(Boolean.TRUE.equals(request.getRepasInclus()));
        existant.setWifiDisponible(Boolean.TRUE.equals(request.getWifiDisponible()));
        existant.setImageUrl(request.getImageUrl());
        if (request.getStatut() != null) existant.setStatut(request.getStatut());

        if (request.getBillets() != null && !request.getBillets().isEmpty()) {
            existant.getBillets().clear();
            for (OffreBilletDto bDto : request.getBillets()) {
                existant.addBillet(voyageMapper.toBilletEntity(bDto));
            }
        }

        return voyageMapper.toResponse(voyageRepository.save(existant));
    }

    public void supprimerVoyage(Long id) {
        if (!voyageRepository.existsById(id)) {
            throw new ResourceNotFoundException("Offre de voyage introuvable avec l'ID : " + id);
        }
        voyageRepository.deleteById(id);
    }

    // =========================================================================
    // 4. COMPARAISON D'OFFRES CÔTE-À-CÔTE
    // =========================================================================

    @Transactional(readOnly = true)
    public ComparaisonResponse comparerOffres(List<Long> ids) {
        if (ids == null || ids.size() < 2) {
            throw new InvalidComparisonRequestException("Veuillez sélectionner au moins 2 offres à comparer.");
        }
        if (ids.size() > 5) {
            throw new InvalidComparisonRequestException("Vous pouvez comparer jusqu'à 5 offres simultanément.");
        }

        List<VoyageResponse> offres = ids.stream()
                .distinct()
                .map(id -> voyageRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Offre introuvable avec l'ID : " + id)))
                .map(voyageMapper::toResponse)
                .collect(Collectors.toList());

        if (offres.size() < 2) {
            throw new InvalidComparisonRequestException("Veuillez fournir au moins 2 identifiants d'offres distincts.");
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
        int ecartDuree = plusLente.getDureeMinutes() - plusRapide.getDureeMinutes();

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
    // 5. SYSTÈME DE SUGGESTIONS ET RECOMMANDATIONS INTELLIGENTES
    // =========================================================================

    @Transactional(readOnly = true)
    public SuggestionsResponse obtenirSuggestionsGlobales(
            String villeDestination,
            BigDecimal budgetMax,
            TypeTransport typeTransport) {

        List<VoyageResponse> toutes = voyageRepository.findAll().stream()
                .map(voyageMapper::toResponse)
                .collect(Collectors.toList());

        List<VoyageResponse> tendances = toutes.stream()
                .sorted(Comparator.comparing(VoyageResponse::getNombreConsultations, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(VoyageResponse::getNoteMoyenne, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(4)
                .collect(Collectors.toList());

        List<VoyageResponse> promotions = toutes.stream()
                .filter(v -> Boolean.TRUE.equals(v.getEnPromotion()))
                .sorted(Comparator.comparing(VoyageResponse::getPourcentageReduction, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(4)
                .collect(Collectors.toList());

        BigDecimal seuilPetitBudget = (budgetMax != null) ? budgetMax : BigDecimal.valueOf(500);
        List<VoyageResponse> petitsBudgets = toutes.stream()
                .filter(v -> v.getPrixEffectif().compareTo(seuilPetitBudget) <= 0)
                .sorted(Comparator.comparing(VoyageResponse::getPrixEffectif))
                .limit(4)
                .collect(Collectors.toList());

        List<VoyageResponse> meilleurQualitePrix = toutes.stream()
                .sorted(Comparator.comparing(VoyageResponse::getScoreQualitePrix, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(4)
                .collect(Collectors.toList());

        List<VoyageResponse> similaires = toutes.stream()
                .filter(v -> (villeDestination == null || villeDestination.isBlank()
                        || v.getVilleDestination().equalsIgnoreCase(villeDestination.trim()))
                        && (typeTransport == null || v.getTypeTransport() == typeTransport))
                .sorted(Comparator.comparing(VoyageResponse::getScoreQualitePrix).reversed())
                .limit(4)
                .collect(Collectors.toList());

        String contexte = (villeDestination != null && !villeDestination.isBlank())
                ? "Suggestions personnalisées pour votre recherche vers " + villeDestination
                : "Suggestions intelligentes basées sur les tendances, promotions et meilleurs scores qualité/prix";

        return SuggestionsResponse.builder()
                .contexteSuggestion(contexte)
                .offresTendances(tendances)
                .meilleuresPromotions(promotions)
                .petitsBudgets(petitsBudgets)
                .offresSimilaires(similaires)
                .meilleurRapportQualitePrix(meilleurQualitePrix)
                .build();
    }

    @Transactional(readOnly = true)
    public SuggestionsResponse obtenirSuggestionsPourOffre(Long voyageId) {
        Voyage reference = voyageRepository.findById(voyageId)
                .orElseThrow(() -> new ResourceNotFoundException("Offre de voyage introuvable avec l'ID : " + voyageId));

        List<VoyageResponse> autresOffres = voyageRepository.findAll().stream()
                .filter(v -> !v.getId().equals(voyageId))
                .map(voyageMapper::toResponse)
                .collect(Collectors.toList());

        // Offres similaires : même destination ou même pays ou même type de transport
        List<VoyageResponse> similaires = autresOffres.stream()
                .filter(v -> v.getVilleDestination().equalsIgnoreCase(reference.getVilleDestination())
                        || v.getPaysDestination().equalsIgnoreCase(reference.getPaysDestination())
                        || v.getTypeTransport() == reference.getTypeTransport())
                .sorted(Comparator.comparing(
                        (VoyageResponse v) -> v.getVilleDestination().equalsIgnoreCase(reference.getVilleDestination()) ? 0 : 1)
                        .thenComparing(VoyageResponse::getScoreQualitePrix, Comparator.reverseOrder()))
                .limit(4)
                .collect(Collectors.toList());

        // Alternatives moins chères
        BigDecimal prixRef = reference.getPrixEffectif();
        List<VoyageResponse> moinsCheres = autresOffres.stream()
                .filter(v -> v.getPrixEffectif().compareTo(prixRef) < 0)
                .sorted(Comparator.comparing(VoyageResponse::getPrixEffectif))
                .limit(4)
                .collect(Collectors.toList());

        // Meilleur rapport qualité/prix
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
    // 6. STATISTIQUES ET VALEURS DE FILTRES DYNAMIQUES
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
                .totalOffresBillets(offreBilletRepository.count())
                .totalOffresEnPromotion(totalPromos)
                .totalRecherchesEffectuees(historiqueRepository.count())
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
    // 7. COMPATIBILITÉ INTER-SERVICES (MODULE 3 RÉSERVATION & MODULE 1 BAGAGES)
    // =========================================================================

    @Transactional(readOnly = true)
    public Map<String, Object> verifierDisponibilitePourReservation(Long idVoyage) {
        Voyage v = voyageRepository.findById(idVoyage)
                .orElseThrow(() -> new ResourceNotFoundException("Voyage introuvable avec id_voyage : " + idVoyage));

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
        res.put("max_bagage_autorise_kg", 30.0); // Règle métier du Module 1 (BagageService.MAX_ALLOWED_WEIGHT)
        return res;
    }

    public Map<String, Object> mettreAJourPlacesDisponibles(Long idVoyage, int placesReservees) {
        Voyage v = voyageRepository.findById(idVoyage)
                .orElseThrow(() -> new ResourceNotFoundException("Voyage introuvable avec id_voyage : " + idVoyage));

        int restantes = Math.max(0, (v.getPlacesDisponibles() != null ? v.getPlacesDisponibles() : 0) - placesReservees);
        v.setPlacesDisponibles(restantes);
        voyageRepository.save(v);

        return verifierDisponibilitePourReservation(idVoyage);
    }
}
