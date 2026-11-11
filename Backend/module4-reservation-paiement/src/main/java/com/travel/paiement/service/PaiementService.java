package com.travel.paiement.service;

import com.travel.paiement.dto.PaiementRequest;
import com.travel.paiement.dto.PaiementResponse;
import com.travel.paiement.dto.PaymeeCheckoutRequest;
import com.travel.paiement.dto.PaymeeCheckoutResponse;
import com.travel.paiement.dto.PaymeeWebhookRequest;
import com.travel.paiement.entity.Paiement;
import com.travel.paiement.exception.ResourceNotFoundException;
import com.travel.paiement.mapper.PaiementMapper;
import com.travel.paiement.repository.PaiementRepository;
import com.travel.paiement.config.PaymeeProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final PaymeeClient paymeeClient;
    private final PaymeeProperties paymeeProperties;

    public PaiementService(
            PaiementRepository paiementRepository,
            PaymeeClient paymeeClient,
            PaymeeProperties paymeeProperties
    ) {
        this.paiementRepository = paiementRepository;
        this.paymeeClient = paymeeClient;
        this.paymeeProperties = paymeeProperties;
    }

    public PaiementResponse create(PaiementRequest request) {
        Paiement paiement = new Paiement();
        paiement.setId(UUID.randomUUID().toString());
        paiement.setMontant(request.montant());
        paiement.setDatePaiement(request.datePaiement());
        paiement.setStatut(request.statut());
        paiement.setReference(request.reference());
        paiement.setPaiementEnLigne(false);
        return PaiementMapper.toResponse(paiementRepository.save(paiement));
    }

    public com.travel.paiement.dto.BagagePaiementDto recordBaggagePayment(com.travel.paiement.dto.BagagePaiementDto dto) {
        String reference = "BAG-" + (dto.bagageId() != null ? dto.bagageId() : UUID.randomUUID().toString().substring(0, 8));
        PaiementResponse saved = create(new PaiementRequest(
                java.math.BigDecimal.valueOf(dto.montant()),
                java.time.LocalDateTime.now(),
                "EN_ATTENTE",
                reference));
        return new com.travel.paiement.dto.BagagePaiementDto(
                saved.id(),
                dto.bagageId(),
                dto.reservationId(),
                dto.montant(),
                dto.devise() != null ? dto.devise() : "TND",
                saved.statut(),
                "Montant bagage enregistre dans le module Paiement");
    }

    public PaymeeCheckoutResponse createPaymeeCheckout(PaymeeCheckoutRequest request) {
        paymeeClient.validateConfiguration();

        Paiement paiement = new Paiement();
        paiement.setId(UUID.randomUUID().toString());
        paiement.setMontant(request.montant());
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setStatut("En attente");
        paiement.setReference(request.reference());
        paiement.setPaiementEnLigne(true);
        paiement = paiementRepository.save(paiement);

        String encodedId = URLEncoder.encode(paiement.getId(), StandardCharsets.UTF_8);
        String frontendUrl = removeTrailingSlash(paymeeProperties.getFrontendBaseUrl());
        String returnUrl = frontendUrl + "/?paiement=" + encodedId;
        String cancelUrl = frontendUrl + "/?paiementAnnule=" + encodedId;

        try {
            PaymeeClient.CheckoutSession checkout = paymeeClient.createCheckout(
                    request,
                    paiement.getId(),
                    returnUrl,
                    cancelUrl
            );
            paiement.setPaymeeToken(checkout.token());
            paiementRepository.save(paiement);
            return new PaymeeCheckoutResponse(paiement.getId(), checkout.paymentUrl());
        } catch (PaymeeIntegrationException exception) {
            paiementRepository.delete(paiement);
            throw exception;
        }
    }

    public List<PaiementResponse> findAll() {
        List<PaiementResponse> result = new ArrayList<>();
        paiementRepository.findAll().forEach(paiement -> result.add(PaiementMapper.toResponse(paiement)));
        return result;
    }

    public PaiementResponse findById(String id) {
        return PaiementMapper.toResponse(getPaiement(id));
    }

    public PaiementResponse update(String id, PaiementRequest request) {
        Paiement paiement = getPaiement(id);
        if (Boolean.TRUE.equals(paiement.getPaiementEnLigne())) {
            throw new IllegalStateException(
                    "Le statut d'un paiement Paymee est mis à jour uniquement après confirmation du prestataire."
            );
        }
        paiement.setMontant(request.montant());
        paiement.setDatePaiement(request.datePaiement());
        paiement.setStatut(request.statut());
        paiement.setReference(request.reference());
        return PaiementMapper.toResponse(paiementRepository.save(paiement));
    }

    public void delete(String id) {
        Paiement paiement = getPaiement(id);
        if (Boolean.TRUE.equals(paiement.getPaiementEnLigne())) {
            throw new IllegalStateException("Un paiement Paymee ne peut pas être supprimé.");
        }
        paiementRepository.delete(paiement);
    }

    public PaiementResponse verifyPaymeePayment(String id) {
        Paiement paiement = getPaiement(id);
        if (!Boolean.TRUE.equals(paiement.getPaiementEnLigne()) || isBlank(paiement.getPaymeeToken())) {
            throw new IllegalStateException("Ce paiement ne possède pas de session Paymee à vérifier.");
        }

        PaymeeClient.PaymentCheck check = paymeeClient.checkPayment(paiement.getPaymeeToken());
        if (Boolean.TRUE.equals(check.paymentStatus())) {
            confirmPayment(paiement, check);
        }
        return PaiementMapper.toResponse(paiement);
    }

    public PaiementResponse handlePaymeeWebhook(PaymeeWebhookRequest webhook) {
        if (!paymeeClient.isValidWebhookChecksum(
                webhook.token(),
                webhook.paymentStatus(),
                webhook.checkSum()
        )) {
            throw new InvalidPaymeeWebhookException("Signature Paymee invalide.");
        }

        Paiement paiement = paiementRepository.findByPaymeeToken(webhook.token())
                .orElseThrow(() -> new ResourceNotFoundException("Session de paiement introuvable."));

        PaymeeClient.PaymentCheck check = paymeeClient.checkPayment(webhook.token());
        if (Boolean.TRUE.equals(check.paymentStatus())) {
            confirmPayment(paiement, check);
        } else if (!webhook.paymentStatus() && !"Payé".equals(paiement.getStatut())) {
            paiement.setStatut("Échoué");
            paiement.setDatePaiement(LocalDateTime.now());
            paiement.setTransactionId(webhook.transactionId());
            paiementRepository.save(paiement);
        }
        return PaiementMapper.toResponse(paiement);
    }

    private Paiement getPaiement(String id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement introuvable avec l'ID : " + id));
    }

    private void confirmPayment(Paiement paiement, PaymeeClient.PaymentCheck check) {
        BigDecimal confirmedAmount = check.amount();
        if (confirmedAmount == null || confirmedAmount.compareTo(paiement.getMontant()) != 0) {
            throw new PaymeeIntegrationException(
                    "Le montant confirmé par Paymee ne correspond pas au montant du paiement."
            );
        }
        paiement.setStatut("Payé");
        paiement.setDatePaiement(LocalDateTime.now());
        if (!isBlank(check.transactionId())) {
            paiement.setTransactionId(check.transactionId());
        }
        paiementRepository.save(paiement);
    }

    private String removeTrailingSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}