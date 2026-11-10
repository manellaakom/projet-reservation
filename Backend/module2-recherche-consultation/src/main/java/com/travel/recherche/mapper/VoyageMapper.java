package com.travel.recherche.mapper;

import com.travel.recherche.dto.OffreBilletDto;
import com.travel.recherche.dto.VoyageRequest;
import com.travel.recherche.dto.VoyageResponse;
import com.travel.recherche.entity.OffreBillet;
import com.travel.recherche.entity.Voyage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VoyageMapper {

    public Voyage toEntity(VoyageRequest request) {
        Voyage voyage = Voyage.builder()
                .codeVoyage(request.getCodeVoyage())
                .titre(request.getTitre())
                .description(request.getDescription())
                .villeDepart(request.getVilleDepart())
                .villeDestination(request.getVilleDestination())
                .paysDestination(request.getPaysDestination())
                .compagnie(request.getCompagnie())
                .typeTransport(request.getTypeTransport())
                .categorie(request.getCategorie())
                .dateDepart(request.getDateDepart())
                .dateArrivee(request.getDateArrivee())
                .dureeMinutes(request.getDureeMinutes())
                .escales(request.getEscales() != null ? request.getEscales() : 0)
                .prixBase(request.getPrixBase())
                .prixPromo(request.getPrixPromo())
                .enPromotion(Boolean.TRUE.equals(request.getEnPromotion()))
                .placesDisponibles(request.getPlacesDisponibles())
                .noteMoyenne(request.getNoteMoyenne() != null ? request.getNoteMoyenne() : 4.5)
                .nombreAvis(request.getNombreAvis() != null ? request.getNombreAvis() : 10)
                .bagageCabineInclus(request.getBagageCabineInclus() != null ? request.getBagageCabineInclus() : true)
                .bagageSouteInclus(Boolean.TRUE.equals(request.getBagageSouteInclus()))
                .annulationGratuite(Boolean.TRUE.equals(request.getAnnulationGratuite()))
                .repasInclus(Boolean.TRUE.equals(request.getRepasInclus()))
                .wifiDisponible(Boolean.TRUE.equals(request.getWifiDisponible()))
                .imageUrl(request.getImageUrl())
                .statut(request.getStatut())
                .build();

        if (request.getBillets() != null) {
            for (OffreBilletDto billetDto : request.getBillets()) {
                voyage.addBillet(toBilletEntity(billetDto));
            }
        }
        return voyage;
    }

    public OffreBillet toBilletEntity(OffreBilletDto dto) {
        return OffreBillet.builder()
                .codeOffreBillet(dto.getCodeOffreBillet())
                .classe(dto.getClasse())
                .prix(dto.getPrix())
                .placesRestantes(dto.getPlacesRestantes())
                .bagageCabineKg(dto.getBagageCabineKg() != null ? dto.getBagageCabineKg() : 8)
                .bagageSouteKg(dto.getBagageSouteKg() != null ? dto.getBagageSouteKg() : 0)
                .modifiable(Boolean.TRUE.equals(dto.getModifiable()))
                .remboursable(Boolean.TRUE.equals(dto.getRemboursable()))
                .choixSiegeGratuit(Boolean.TRUE.equals(dto.getChoixSiegeGratuit()))
                .accesSalonVip(Boolean.TRUE.equals(dto.getAccesSalonVip()))
                .conditionsTarifaires(dto.getConditionsTarifaires())
                .build();
    }

    public OffreBilletDto toBilletDto(OffreBillet billet) {
        Voyage v = billet.getVoyage();
        return OffreBilletDto.builder()
                .id(billet.getId())
                .codeOffreBillet(billet.getCodeOffreBillet())
                .classe(billet.getClasse())
                .prix(billet.getPrix())
                .placesRestantes(billet.getPlacesRestantes())
                .bagageCabineKg(billet.getBagageCabineKg())
                .bagageSouteKg(billet.getBagageSouteKg())
                .modifiable(billet.getModifiable())
                .remboursable(billet.getRemboursable())
                .choixSiegeGratuit(billet.getChoixSiegeGratuit())
                .accesSalonVip(billet.getAccesSalonVip())
                .conditionsTarifaires(billet.getConditionsTarifaires())
                .voyageId(v != null ? v.getId() : null)
                .codeVoyage(v != null ? v.getCodeVoyage() : null)
                .titreVoyage(v != null ? v.getTitre() : null)
                .villeDepart(v != null ? v.getVilleDepart() : null)
                .villeDestination(v != null ? v.getVilleDestination() : null)
                .compagnie(v != null ? v.getCompagnie() : null)
                .typeTransport(v != null ? v.getTypeTransport() : null)
                .dateDepart(v != null ? v.getDateDepart() : null)
                .dateArrivee(v != null ? v.getDateArrivee() : null)
                .build();
    }

    public VoyageResponse toResponse(Voyage voyage) {
        BigDecimal prixEffectif = voyage.getPrixEffectif();
        int pourcentageReduction = calculerReduction(voyage.getPrixBase(), voyage.getPrixPromo(), voyage.getEnPromotion());
        int scoreQualitePrix = calculerScoreQualitePrix(voyage, prixEffectif, pourcentageReduction);
        List<String> badges = genererBadges(voyage, pourcentageReduction, scoreQualitePrix);
        List<String> avantages = genererAvantagesCles(voyage);

        List<OffreBilletDto> billetsDto = voyage.getBillets() != null
                ? voyage.getBillets().stream().map(this::toBilletDto).collect(Collectors.toList())
                : new ArrayList<>();

        return VoyageResponse.builder()
                .id(voyage.getId())
                .codeVoyage(voyage.getCodeVoyage())
                .titre(voyage.getTitre())
                .description(voyage.getDescription())
                .villeDepart(voyage.getVilleDepart())
                .villeDestination(voyage.getVilleDestination())
                .paysDestination(voyage.getPaysDestination())
                .compagnie(voyage.getCompagnie())
                .typeTransport(voyage.getTypeTransport())
                .categorie(voyage.getCategorie())
                .dateDepart(voyage.getDateDepart())
                .dateArrivee(voyage.getDateArrivee())
                .dureeMinutes(voyage.getDureeMinutes())
                .dureeFormatee(formaterDuree(voyage.getDureeMinutes()))
                .escales(voyage.getEscales())
                .prixBase(voyage.getPrixBase())
                .prixPromo(voyage.getPrixPromo())
                .prixEffectif(prixEffectif)
                .pourcentageReduction(pourcentageReduction)
                .enPromotion(voyage.getEnPromotion())
                .placesDisponibles(voyage.getPlacesDisponibles())
                .noteMoyenne(voyage.getNoteMoyenne())
                .nombreAvis(voyage.getNombreAvis())
                .nombreConsultations(voyage.getNombreConsultations())
                .bagageCabineInclus(voyage.getBagageCabineInclus())
                .bagageSouteInclus(voyage.getBagageSouteInclus())
                .annulationGratuite(voyage.getAnnulationGratuite())
                .repasInclus(voyage.getRepasInclus())
                .wifiDisponible(voyage.getWifiDisponible())
                .imageUrl(voyage.getImageUrl())
                .statut(voyage.getStatut())
                .scoreQualitePrix(scoreQualitePrix)
                .badgesRecommandation(badges)
                .avantagesCles(avantages)
                .billets(billetsDto)
                .build();
    }

    private int calculerReduction(BigDecimal prixBase, BigDecimal prixPromo, Boolean enPromotion) {
        if (Boolean.TRUE.equals(enPromotion) && prixBase != null && prixPromo != null
                && prixBase.compareTo(BigDecimal.ZERO) > 0 && prixPromo.compareTo(prixBase) < 0) {
            BigDecimal diff = prixBase.subtract(prixPromo);
            return diff.multiply(BigDecimal.valueOf(100))
                    .divide(prixBase, 0, RoundingMode.HALF_UP)
                    .intValue();
        }
        return 0;
    }

    public int calculerScoreQualitePrix(Voyage v, BigDecimal prixEffectif, int reduction) {
        double score = 55.0;
        // Note moyenne sur 5 -> jusqu'à +20 pts
        if (v.getNoteMoyenne() != null) {
            score += (v.getNoteMoyenne() - 3.5) * 13.0;
        }
        // Promotion -> jusqu'à +12 pts
        score += Math.min(12, reduction * 0.5);
        // Trajet direct -> +6 pts
        if (v.getEscales() != null && v.getEscales() == 0) {
            score += 6;
        }
        // Services inclus
        if (Boolean.TRUE.equals(v.getBagageSouteInclus())) score += 4;
        if (Boolean.TRUE.equals(v.getAnnulationGratuite())) score += 4;
        if (Boolean.TRUE.equals(v.getRepasInclus())) score += 2;
        if (Boolean.TRUE.equals(v.getWifiDisponible())) score += 2;

        // Bonus tarif compétitif
        if (prixEffectif != null && prixEffectif.doubleValue() < 450.0) {
            score += 5;
        }
        return (int) Math.max(40, Math.min(99, Math.round(score)));
    }

    private List<String> genererBadges(Voyage v, int reduction, int score) {
        List<String> badges = new ArrayList<>();
        if (Boolean.TRUE.equals(v.getEnPromotion()) && reduction > 0) {
            badges.add("Promo -" + reduction + "%");
        }
        if (score >= 88) {
            badges.add("Meilleur Rapport Qualité/Prix");
        }
        if (v.getNoteMoyenne() != null && v.getNoteMoyenne() >= 4.8) {
            badges.add("Coup de Cœur Voyageurs");
        }
        if (v.getEscales() != null && v.getEscales() == 0) {
            badges.add("Trajet Direct");
        }
        if (v.getNombreConsultations() != null && v.getNombreConsultations() >= 120) {
            badges.add("Offre Tendance");
        }
        if (v.getPlacesDisponibles() != null && v.getPlacesDisponibles() > 0 && v.getPlacesDisponibles() <= 8) {
            badges.add("Dernières Places");
        }
        return badges;
    }

    private List<String> genererAvantagesCles(Voyage v) {
        List<String> avantages = new ArrayList<>();
        if (v.getEscales() != null && v.getEscales() == 0) {
            avantages.add("Trajet direct sans escale (" + formaterDuree(v.getDureeMinutes()) + ")");
        } else {
            avantages.add(v.getEscales() + " escale(s) — durée totale " + formaterDuree(v.getDureeMinutes()));
        }
        if (Boolean.TRUE.equals(v.getBagageSouteInclus())) {
            avantages.add("Bagage en soute + bagage cabine inclus");
        } else if (Boolean.TRUE.equals(v.getBagageCabineInclus())) {
            avantages.add("Bagage cabine inclus");
        }
        if (Boolean.TRUE.equals(v.getAnnulationGratuite())) {
            avantages.add("Annulation et modification gratuites");
        }
        if (Boolean.TRUE.equals(v.getRepasInclus())) {
            avantages.add("Repas et boissons servis à bord");
        }
        if (Boolean.TRUE.equals(v.getWifiDisponible())) {
            avantages.add("Wi-Fi haut débit à bord");
        }
        return avantages;
    }

    public String formaterDuree(Integer dureeMinutes) {
        if (dureeMinutes == null || dureeMinutes <= 0) {
            return "N/A";
        }
        int heures = dureeMinutes / 60;
        int minutes = dureeMinutes % 60;
        if (heures > 0 && minutes > 0) {
            return heures + "h " + String.format("%02d", minutes) + "min";
        } else if (heures > 0) {
            return heures + "h 00min";
        }
        return minutes + "min";
    }
}
