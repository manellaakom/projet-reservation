package com.travel.recherche.mapper;

import com.travel.recherche.dto.VoyageRequest;
import com.travel.recherche.dto.VoyageResponse;
import com.travel.recherche.entity.Voyage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class VoyageMapper {

    public Voyage toEntity(VoyageRequest request) {
        return Voyage.builder()
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
    }

    public void updateEntity(Voyage voyage, VoyageRequest request) {
        voyage.setCodeVoyage(request.getCodeVoyage());
        voyage.setTitre(request.getTitre());
        voyage.setDescription(request.getDescription());
        voyage.setVilleDepart(request.getVilleDepart());
        voyage.setVilleDestination(request.getVilleDestination());
        voyage.setPaysDestination(request.getPaysDestination());
        voyage.setCompagnie(request.getCompagnie());
        voyage.setTypeTransport(request.getTypeTransport());
        voyage.setCategorie(request.getCategorie());
        voyage.setDateDepart(request.getDateDepart());
        voyage.setDateArrivee(request.getDateArrivee());
        voyage.setDureeMinutes(request.getDureeMinutes());
        if (request.getEscales() != null) {
            voyage.setEscales(request.getEscales());
        }
        voyage.setPrixBase(request.getPrixBase());
        voyage.setPrixPromo(request.getPrixPromo());
        voyage.setEnPromotion(Boolean.TRUE.equals(request.getEnPromotion()));
        voyage.setPlacesDisponibles(request.getPlacesDisponibles());
        if (request.getNoteMoyenne() != null) {
            voyage.setNoteMoyenne(request.getNoteMoyenne());
        }
        if (request.getNombreAvis() != null) {
            voyage.setNombreAvis(request.getNombreAvis());
        }
        if (request.getBagageCabineInclus() != null) {
            voyage.setBagageCabineInclus(request.getBagageCabineInclus());
        }
        voyage.setBagageSouteInclus(Boolean.TRUE.equals(request.getBagageSouteInclus()));
        voyage.setAnnulationGratuite(Boolean.TRUE.equals(request.getAnnulationGratuite()));
        voyage.setRepasInclus(Boolean.TRUE.equals(request.getRepasInclus()));
        voyage.setWifiDisponible(Boolean.TRUE.equals(request.getWifiDisponible()));
        if (request.getImageUrl() != null) {
            voyage.setImageUrl(request.getImageUrl());
        }
        if (request.getStatut() != null) {
            voyage.setStatut(request.getStatut());
        }
    }

    public VoyageResponse toResponse(Voyage voyage) {
        if (voyage == null) {
            return null;
        }

        BigDecimal prixEffectif = voyage.getPrixEffectif();
        int pourcentageReduction = calculerReduction(voyage.getPrixBase(), voyage.getPrixPromo(), voyage.getEnPromotion());
        int scoreQualitePrix = calculerScoreQualitePrix(voyage, prixEffectif, pourcentageReduction);
        List<String> badges = genererBadges(voyage, pourcentageReduction, scoreQualitePrix);
        List<String> avantages = genererAvantagesCles(voyage);

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
        if (v.getNoteMoyenne() != null) {
            score += (v.getNoteMoyenne() - 3.5) * 13.0;
        }
        score += Math.min(12, reduction * 0.5);
        if (v.getEscales() != null && v.getEscales() == 0) {
            score += 6;
        }
        if (Boolean.TRUE.equals(v.getBagageSouteInclus())) score += 4;
        if (Boolean.TRUE.equals(v.getAnnulationGratuite())) score += 4;
        if (Boolean.TRUE.equals(v.getRepasInclus())) score += 2;
        if (Boolean.TRUE.equals(v.getWifiDisponible())) score += 2;

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
