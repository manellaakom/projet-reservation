package com.travel.bagage.service;

import com.travel.bagage.dto.BagageRequest;
import com.travel.bagage.dto.BagageResponse;
import com.travel.bagage.dto.BagageStatisticsResponse;
import com.travel.bagage.entity.Bagage;
import com.travel.bagage.exception.BaggageWeightExceededException;
import com.travel.bagage.exception.ResourceNotFoundException;
import com.travel.bagage.mapper.BagageMapper;
import com.travel.bagage.repository.BagageRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class BagageService {

    public static final double MAX_ALLOWED_WEIGHT = 30.0;

    private final BagageRepository bagageRepository;
    private final BagageMapper bagageMapper;

    public BagageService(BagageRepository bagageRepository, BagageMapper bagageMapper) {
        this.bagageRepository = bagageRepository;
        this.bagageMapper = bagageMapper;
    }

    /**
     * Valide le poids du bagage selon les règles métier.
     */
    public void validateWeight(Double poids) {
        if (poids == null || poids <= 0) {
            throw new BaggageWeightExceededException("Le poids du bagage doit être strictement positif.");
        }
        if (poids > MAX_ALLOWED_WEIGHT) {
            throw new BaggageWeightExceededException("Bagage refusé : le poids (" + poids + " kg) dépasse la limite maximale autorisée de " + MAX_ALLOWED_WEIGHT + " kg.");
        }
    }

    /**
     * Calcule automatiquement le prix du bagage en DT selon son poids :
     * 0–10 kg -> 30 DT
     * >10–20 kg -> 50 DT
     * >20–30 kg -> 70 DT
     * >30 kg -> bagage refusé
     */
    public double calculatePrice(Double poids) {
        validateWeight(poids);
        if (poids <= 10.0) {
            return 30.0;
        } else if (poids <= 20.0) {
            return 50.0;
        } else {
            return 70.0;
        }
    }

    public BagageResponse createBagage(BagageRequest request) {
        validateWeight(request.getPoids());
        double automaticPrice = calculatePrice(request.getPoids());

        Bagage bagage = bagageMapper.toEntity(request);
        bagage.setPrix(automaticPrice);
        Bagage saved = bagageRepository.save(bagage);
        return bagageMapper.toResponse(saved);
    }

    public List<BagageResponse> getAllBagages() {
        List<BagageResponse> responses = new ArrayList<>();
        bagageRepository.findAll().forEach(bagage -> responses.add(bagageMapper.toResponse(bagage)));
        return responses;
    }

    public BagageResponse getBagageById(String id) {
        Bagage bagage = bagageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bagage introuvable avec l'ID : " + id));
        return bagageMapper.toResponse(bagage);
    }

    public BagageResponse updateBagage(String id, BagageRequest request) {
        Bagage bagage = bagageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bagage introuvable avec l'ID : " + id));

        validateWeight(request.getPoids());
        double automaticPrice = calculatePrice(request.getPoids());

        bagage.setReservationId(request.getReservationId());
        bagage.setType(request.getType());
        bagage.setPoids(request.getPoids());
        bagage.setStatut(request.getStatut());
        bagage.setPrix(automaticPrice);

        Bagage updated = bagageRepository.save(bagage);
        return bagageMapper.toResponse(updated);
    }

    public void deleteBagage(String id) {
        Bagage bagage = bagageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bagage introuvable avec l'ID : " + id));
        bagageRepository.delete(bagage);
    }

    public BagageResponse associateToReservation(String id, String reservationId) {
        Bagage bagage = bagageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bagage introuvable avec l'ID : " + id));
        bagage.setReservationId(reservationId);
        Bagage updated = bagageRepository.save(bagage);
        return bagageMapper.toResponse(updated);
    }

    /**
     * Calcule et retourne les statistiques globales des bagages enregistrés.
     */
    public BagageStatisticsResponse getStatistics() {
        List<Bagage> allBagages = new ArrayList<>();
        bagageRepository.findAll().forEach(allBagages::add);

        long totalBagages = allBagages.size();
        double totalWeight = 0.0;
        double totalRevenue = 0.0;
        long pendingBagages = 0;
        long registeredBagages = 0;

        for (Bagage b : allBagages) {
            if (b.getPoids() != null) {
                totalWeight += b.getPoids();
            }
            if (b.getPrix() != null) {
                totalRevenue += b.getPrix();
            }
            if (isPendingStatus(b.getStatut())) {
                pendingBagages++;
            }
            if (isRegisteredStatus(b.getStatut())) {
                registeredBagages++;
            }
        }

        double averageWeight = totalBagages > 0 ? totalWeight / totalBagages : 0.0;

        totalWeight = roundTwoDecimals(totalWeight);
        totalRevenue = roundTwoDecimals(totalRevenue);
        averageWeight = roundTwoDecimals(averageWeight);

        return BagageStatisticsResponse.builder()
                .totalBagages(totalBagages)
                .totalWeight(totalWeight)
                .totalRevenue(totalRevenue)
                .averageWeight(averageWeight)
                .pendingBagages(pendingBagages)
                .registeredBagages(registeredBagages)
                .build();
    }

    private boolean isPendingStatus(String status) {
        if (status == null) return false;
        String s = status.trim().toUpperCase();
        return s.contains("PENDING") || s.contains("ATTENTE");
    }

    private boolean isRegisteredStatus(String status) {
        if (status == null) return false;
        String s = status.trim().toUpperCase();
        return s.contains("REGISTER") || s.contains("ENREGISTR") || s.contains("VALIDE") || s.contains("CONFIRM");
    }

    private double roundTwoDecimals(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
