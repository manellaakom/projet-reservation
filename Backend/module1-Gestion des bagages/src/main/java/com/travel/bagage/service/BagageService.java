package com.travel.bagage.service;

import com.travel.bagage.client.PaymentDto;
import com.travel.bagage.client.PaymentServiceClient;
import com.travel.bagage.client.ReservationDto;
import com.travel.bagage.client.ReservationServiceClient;
import com.travel.bagage.dto.BagageRequest;
import com.travel.bagage.dto.BagageResponse;
import com.travel.bagage.dto.BagageStatisticsResponse;
import com.travel.bagage.entity.Bagage;
import com.travel.bagage.exception.BaggageWeightExceededException;
import com.travel.bagage.exception.ResourceNotFoundException;
import com.travel.bagage.mapper.BagageMapper;
import com.travel.bagage.messaging.BaggageProducer;
import com.travel.bagage.repository.BagageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class BagageService {

    private static final Logger log = LoggerFactory.getLogger(BagageService.class);
    public static final double MAX_ALLOWED_WEIGHT = 30.0;

    private final BagageRepository bagageRepository;
    private final BagageMapper bagageMapper;

    @Autowired(required = false)
    private ReservationServiceClient reservationServiceClient;

    @Autowired(required = false)
    private PaymentServiceClient paymentServiceClient;

    @Autowired(required = false)
    private BaggageProducer baggageProducer;

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

        // Communication synchrone Feign 1 : validation réservation auprès du Module 3
        if (reservationServiceClient != null && request.getReservationId() != null && !request.getReservationId().isBlank()) {
            try {
                ReservationDto resDto = reservationServiceClient.getReservationById(request.getReservationId());
                log.info("Feign 1: Réservation vérifiée avec succès : {}", resDto);
            } catch (Exception e) {
                log.warn("Feign 1: Appel vérification réservation (mode tolérant) : {}", e.getMessage());
            }
        }

        Bagage bagage = bagageMapper.toEntity(request);
        bagage.setPrix(automaticPrice);
        Bagage saved = bagageRepository.save(bagage);

        // Communication synchrone Feign 2 : notification paiement auprès du Module 4
        if (paymentServiceClient != null) {
            try {
                PaymentDto paymentDto = PaymentDto.builder()
                        .bagageId(saved.getId())
                        .reservationId(saved.getReservationId())
                        .montant(saved.getPrix())
                        .devise("TND")
                        .statut("ENREGISTRE")
                        .build();
                PaymentDto payRes = paymentServiceClient.recordBaggagePayment(paymentDto);
                log.info("Feign 2: Notification paiement enregistrée : {}", payRes);
            } catch (Exception e) {
                log.warn("Feign 2: Appel service paiement (mode tolérant) : {}", e.getMessage());
            }
        }

        // Communication asynchrone RabbitMQ 1 : émission événement création bagage
        if (baggageProducer != null) {
            baggageProducer.sendBaggageCreatedEvent(saved.getId(), saved.getReservationId(), saved.getPoids(), saved.getPrix(), saved.getStatut());
        }

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

        // Communication asynchrone RabbitMQ 2 : émission événement mise à jour statut
        if (baggageProducer != null) {
            baggageProducer.sendBaggageStatusUpdatedEvent(updated.getId(), updated.getReservationId(), updated.getStatut(), "Mise à jour données bagage");
        }

        return bagageMapper.toResponse(updated);
    }

    public void deleteBagage(String id) {
        Bagage bagage = bagageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bagage introuvable avec l'ID : " + id));
        bagageRepository.delete(bagage);

        if (baggageProducer != null) {
            baggageProducer.sendBaggageStatusUpdatedEvent(id, bagage.getReservationId(), "DELETED", "Bagage supprimé du système");
        }
    }

    public BagageResponse associateToReservation(String id, String reservationId) {
        Bagage bagage = bagageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bagage introuvable avec l'ID : " + id));

        if (reservationServiceClient != null) {
            try {
                ReservationDto resDto = reservationServiceClient.getReservationById(reservationId);
                log.info("Feign 1: Association validée avec la réservation : {}", resDto);
            } catch (Exception e) {
                log.warn("Feign 1: Tolérance fallback sur association : {}", e.getMessage());
            }
        }

        bagage.setReservationId(reservationId);
        Bagage updated = bagageRepository.save(bagage);

        if (baggageProducer != null) {
            baggageProducer.sendBaggageStatusUpdatedEvent(updated.getId(), reservationId, updated.getStatut(), "Association nouvelle réservation " + reservationId);
        }

        return bagageMapper.toResponse(updated);
    }

    /**
     * Méthode de test directe Feign Scénario 1
     */
    public ReservationDto checkReservation(String reservationId) {
        if (reservationServiceClient != null) {
            return reservationServiceClient.getReservationById(reservationId);
        }
        return new com.travel.bagage.client.ReservationClientFallback().getReservationById(reservationId);
    }

    /**
     * Méthode de test directe Feign Scénario 2
     */
    public PaymentDto notifyPayment(String bagageId) {
        Bagage b = bagageRepository.findById(bagageId)
                .orElseThrow(() -> new ResourceNotFoundException("Bagage introuvable : " + bagageId));

        PaymentDto dto = PaymentDto.builder()
                .bagageId(b.getId())
                .reservationId(b.getReservationId())
                .montant(b.getPrix() != null ? b.getPrix() : calculatePrice(b.getPoids()))
                .devise("TND")
                .statut("CONFIRME")
                .build();

        if (paymentServiceClient != null) {
            return paymentServiceClient.recordBaggagePayment(dto);
        }
        return new com.travel.bagage.client.PaymentClientFallback().recordBaggagePayment(dto);
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
