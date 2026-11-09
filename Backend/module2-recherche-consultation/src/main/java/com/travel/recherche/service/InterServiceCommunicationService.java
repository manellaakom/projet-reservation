package com.travel.recherche.service;

import com.travel.recherche.client.BagageClient;
import com.travel.recherche.client.PaiementClient;
import com.travel.recherche.client.ReservationDisponibiliteClient;
import com.travel.recherche.dto.VoyageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service orchestrant la communication synchrone (OpenFeign) entre le Module 2 et les autres microservices.
 * Valide les exigences de communication synchrone (> 2 scénarios réels).
 */
@Service
public class InterServiceCommunicationService {

    private static final Logger log = LoggerFactory.getLogger(InterServiceCommunicationService.class);

    private final ReservationDisponibiliteClient reservationClient;
    private final BagageClient bagageClient;
    private final PaiementClient paiementClient;
    private final RechercheConsultationService rechercheService;

    public InterServiceCommunicationService(ReservationDisponibiliteClient reservationClient,
                                            BagageClient bagageClient,
                                            PaiementClient paiementClient,
                                            RechercheConsultationService rechercheService) {
        this.reservationClient = reservationClient;
        this.bagageClient = bagageClient;
        this.paiementClient = paiementClient;
        this.rechercheService = rechercheService;
    }

    /**
     * Scénario 1 (Feign) : Vérifier la disponibilité en temps réel via Module 3 (FastAPI Réservation)
     */
    public Map<String, Object> verifierDisponibiliteVoyage(Long voyageId) {
        Map<String, Object> result = new HashMap<>();
        result.put("voyageId", voyageId);
        result.put("source", "Feign -> Module 3 (FastAPI RESERVATION-SERVICE)");

        try {
            Map<String, Object> dispo = reservationClient.getDisponibiliteByVoyage(voyageId);
            result.put("status", "SUCCESS");
            result.put("data", dispo);
        } catch (Exception e) {
            log.warn("Module 3 indisponible, utilisation du fallback local: {}", e.getMessage());
            VoyageResponse v = rechercheService.consulterVoyageParId(voyageId);
            result.put("status", "FALLBACK");
            result.put("message", "Module 3 hors ligne (Mode Résilience actif)");
            result.put("placesRestantesCatalogue", v.getPlacesDisponibles());
            result.put("estDisponible", v.getPlacesDisponibles() != null && v.getPlacesDisponibles() > 0);
        }
        return result;
    }

    /**
     * Scénario 2 (Feign) : Récupérer les options bagages et statistiques via Module 1 (Gestion des Bagages)
     */
    public Map<String, Object> consulterBagagesEtStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("source", "Feign -> Module 1 (gestion-bagages sur port 8081)");

        try {
            Map<String, Object> stats = bagageClient.getBagageStatistics();
            List<Map<String, Object>> bagages = bagageClient.getAllBagages();
            result.put("status", "SUCCESS");
            result.put("statistiques", stats);
            result.put("bagagesEnregistres", bagages);
        } catch (Exception e) {
            log.warn("Module 1 indisponible, utilisation du fallback: {}", e.getMessage());
            result.put("status", "FALLBACK");
            result.put("message", "Module 1 bagages hors ligne (Mode Résilience actif)");
            result.put("poidsMaxDefautKg", 23);
            result.put("bagageCabineInclus", true);
        }
        return result;
    }

    /**
     * Scénario 3 (Feign) : Consulter l'historique des paiements via Module 4 (Paiement)
     */
    public Map<String, Object> consulterPaiements() {
        Map<String, Object> result = new HashMap<>();
        result.put("source", "Feign -> Module 4 (reservation-paiement sur port 8084)");

        try {
            List<Map<String, Object>> paiements = paiementClient.getAllPaiements();
            result.put("status", "SUCCESS");
            result.put("totalPaiements", paiements.size());
            result.put("paiements", paiements);
        } catch (Exception e) {
            log.warn("Module 4 indisponible, utilisation du fallback: {}", e.getMessage());
            result.put("status", "FALLBACK");
            result.put("message", "Module 4 paiements hors ligne (Mode Résilience actif)");
            result.put("modesAcceptes", List.of("CARTE_BANCAIRE", "PAYPAL", "VIREMENT"));
        }
        return result;
    }

    /**
     * Synthèse complète : Regroupement multi-microservices pour un voyage donné
     */
    public Map<String, Object> syntheseCompleteVoyage(Long voyageId) {
        VoyageResponse voyage = rechercheService.consulterVoyageParId(voyageId);
        Map<String, Object> dispo = verifierDisponibiliteVoyage(voyageId);
        Map<String, Object> bagages = consulterBagagesEtStats();

        Map<String, Object> synthese = new HashMap<>();
        synthese.put("voyage", voyage);
        synthese.put("disponibiliteTempsReel", dispo);
        synthese.put("optionsBagages", bagages);
        synthese.put("microservicesImpliques", List.of(
                "Module 2 (Recherche)",
                "Module 3 (Réservation)",
                "Module 1 (Bagages)"
        ));
        return synthese;
    }
}
