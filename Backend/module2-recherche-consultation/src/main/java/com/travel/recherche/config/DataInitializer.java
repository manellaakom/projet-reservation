package com.travel.recherche.config;

import com.travel.recherche.entity.Voyage;
import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.StatutOffre;
import com.travel.recherche.enums.TypeTransport;
import com.travel.recherche.repository.VoyageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initCatalogueVoyages(VoyageRepository voyageRepository) {
        return args -> {
            if (voyageRepository.count() > 0) {
                return;
            }

            // 1. Tunis -> Paris (Vol direct Tunisair)
            Voyage v1 = Voyage.builder()
                    .codeVoyage("VOY-TUN-PAR-101")
                    .titre("Vol Direct Tunis → Paris Charles de Gaulle")
                    .description("Vol régulier direct opéré par Tunisair. Repas chaud inclus, bagage cabine 8 kg et bagage en soute 23 kg.")
                    .villeDepart("Tunis")
                    .villeDestination("Paris")
                    .paysDestination("France")
                    .compagnie("Tunisair")
                    .typeTransport(TypeTransport.VOL)
                    .categorie(CategorieOffre.CONFORT)
                    .dateDepart(LocalDateTime.of(2026, 11, 15, 8, 30))
                    .dateArrivee(LocalDateTime.of(2026, 11, 15, 11, 5))
                    .dureeMinutes(155)
                    .escales(0)
                    .prixBase(BigDecimal.valueOf(620))
                    .prixPromo(BigDecimal.valueOf(465))
                    .enPromotion(true)
                    .placesDisponibles(28)
                    .noteMoyenne(4.6)
                    .nombreAvis(184)
                    .nombreConsultations(245)
                    .bagageCabineInclus(true)
                    .bagageSouteInclus(true)
                    .annulationGratuite(true)
                    .repasInclus(true)
                    .wifiDisponible(false)
                    .imageUrl("https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=900&q=80")
                    .statut(StatutOffre.DISPONIBLE)
                    .build();

            // 2. Tunis -> Rome (Vol direct Nouvelair)
            Voyage v2 = Voyage.builder()
                    .codeVoyage("VOY-TUN-ROM-102")
                    .titre("Vol Direct Tunis → Rome Fiumicino")
                    .description("Liaison rapide vers la ville éternelle en 1h20. Idéal pour escapade de week-end.")
                    .villeDepart("Tunis")
                    .villeDestination("Rome")
                    .paysDestination("Italie")
                    .compagnie("Nouvelair")
                    .typeTransport(TypeTransport.VOL)
                    .categorie(CategorieOffre.ECONOMIQUE)
                    .dateDepart(LocalDateTime.of(2026, 11, 18, 10, 0))
                    .dateArrivee(LocalDateTime.of(2026, 11, 18, 11, 20))
                    .dureeMinutes(80)
                    .escales(0)
                    .prixBase(BigDecimal.valueOf(390))
                    .prixPromo(BigDecimal.valueOf(320))
                    .enPromotion(true)
                    .placesDisponibles(19)
                    .noteMoyenne(4.4)
                    .nombreAvis(96)
                    .nombreConsultations(180)
                    .bagageCabineInclus(true)
                    .bagageSouteInclus(false)
                    .annulationGratuite(false)
                    .repasInclus(false)
                    .wifiDisponible(false)
                    .imageUrl("https://images.unsplash.com/photo-1552832230-c0197dd311b5?auto=format&fit=crop&w=900&q=80")
                    .statut(StatutOffre.DISPONIBLE)
                    .build();

            // 3. Tunis -> Istanbul (Vol direct Turkish Airlines)
            Voyage v3 = Voyage.builder()
                    .codeVoyage("VOY-TUN-IST-103")
                    .titre("Vol Tunis → Istanbul Nouvel Aéroport")
                    .description("Expérience premium avec service de bord réputé, écrans individuels et repas gastronomique.")
                    .villeDepart("Tunis")
                    .villeDestination("Istanbul")
                    .paysDestination("Turquie")
                    .compagnie("Turkish Airlines")
                    .typeTransport(TypeTransport.VOL)
                    .categorie(CategorieOffre.PREMIUM)
                    .dateDepart(LocalDateTime.of(2026, 11, 20, 16, 45))
                    .dateArrivee(LocalDateTime.of(2026, 11, 20, 21, 30))
                    .dureeMinutes(165)
                    .escales(0)
                    .prixBase(BigDecimal.valueOf(880))
                    .prixPromo(null)
                    .enPromotion(false)
                    .placesDisponibles(14)
                    .noteMoyenne(4.8)
                    .nombreAvis(310)
                    .nombreConsultations(412)
                    .bagageCabineInclus(true)
                    .bagageSouteInclus(true)
                    .annulationGratuite(true)
                    .repasInclus(true)
                    .wifiDisponible(true)
                    .imageUrl("https://images.unsplash.com/photo-1524231757912-21f4fe3a7200?auto=format&fit=crop&w=900&q=80")
                    .statut(StatutOffre.DISPONIBLE)
                    .build();

            // 4. Paris -> Lyon (TGV InOui)
            Voyage v4 = Voyage.builder()
                    .codeVoyage("VOY-PAR-LYO-104")
                    .titre("TGV Direct Paris Gare de Lyon → Lyon Part-Dieu")
                    .description("Trajet à grande vitesse en 1h57 au centre de Lyon. Wi-Fi haut débit et prises électriques.")
                    .villeDepart("Paris")
                    .villeDestination("Lyon")
                    .paysDestination("France")
                    .compagnie("SNCF Voyageurs")
                    .typeTransport(TypeTransport.TRAIN)
                    .categorie(CategorieOffre.CONFORT)
                    .dateDepart(LocalDateTime.of(2026, 11, 22, 9, 0))
                    .dateArrivee(LocalDateTime.of(2026, 11, 22, 10, 57))
                    .dureeMinutes(117)
                    .escales(0)
                    .prixBase(BigDecimal.valueOf(85))
                    .prixPromo(BigDecimal.valueOf(59))
                    .enPromotion(true)
                    .placesDisponibles(42)
                    .noteMoyenne(4.7)
                    .nombreAvis(520)
                    .nombreConsultations(350)
                    .bagageCabineInclus(true)
                    .bagageSouteInclus(true)
                    .annulationGratuite(true)
                    .repasInclus(false)
                    .wifiDisponible(true)
                    .imageUrl("https://images.unsplash.com/photo-1549877452-9c387954fbc2?auto=format&fit=crop&w=900&q=80")
                    .statut(StatutOffre.DISPONIBLE)
                    .build();

            // 5. Paris -> Londres (Eurostar Direct)
            Voyage v5 = Voyage.builder()
                    .codeVoyage("VOY-PAR-LON-105")
                    .titre("Eurostar Paris Gare du Nord → Londres St Pancras")
                    .description("Reliez Paris à Londres en 2h15 sous la Manche. Arrivée directement au cœur de Londres.")
                    .villeDepart("Paris")
                    .villeDestination("Londres")
                    .paysDestination("Royaume-Uni")
                    .compagnie("Eurostar")
                    .typeTransport(TypeTransport.TRAIN)
                    .categorie(CategorieOffre.PREMIUM)
                    .dateDepart(LocalDateTime.of(2026, 11, 25, 7, 12))
                    .dateArrivee(LocalDateTime.of(2026, 11, 25, 8, 30))
                    .dureeMinutes(138)
                    .escales(0)
                    .prixBase(BigDecimal.valueOf(210))
                    .prixPromo(BigDecimal.valueOf(160))
                    .enPromotion(true)
                    .placesDisponibles(12)
                    .noteMoyenne(4.9)
                    .nombreAvis(415)
                    .nombreConsultations(290)
                    .bagageCabineInclus(true)
                    .bagageSouteInclus(true)
                    .annulationGratuite(false)
                    .repasInclus(true)
                    .wifiDisponible(true)
                    .imageUrl("https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=900&q=80")
                    .statut(StatutOffre.DISPONIBLE)
                    .build();

            voyageRepository.saveAll(List.of(v1, v2, v3, v4, v5));
        };
    }
}
