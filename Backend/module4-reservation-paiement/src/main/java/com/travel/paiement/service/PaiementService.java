package com.travel.paiement.service;

import com.travel.paiement.dto.PaiementRequest;
import com.travel.paiement.dto.PaiementResponse;
import com.travel.paiement.entity.Paiement;
import com.travel.paiement.entity.Transaction;
import com.travel.paiement.exception.ResourceNotFoundException;
import com.travel.paiement.mapper.PaiementMapper;
import com.travel.paiement.repository.PaiementRepository;
import com.travel.paiement.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final TransactionRepository transactionRepository;

    public PaiementService(PaiementRepository paiementRepository, TransactionRepository transactionRepository) {
        this.paiementRepository = paiementRepository;
        this.transactionRepository = transactionRepository;
    }

    public PaiementResponse create(PaiementRequest request) {
        Paiement paiement = new Paiement(UUID.randomUUID().toString(), request.montant(),
                request.datePaiement(), request.statut(), request.reference(), null);
        return PaiementMapper.toResponse(paiementRepository.save(paiement));
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
        paiement.setMontant(request.montant());
        paiement.setDatePaiement(request.datePaiement());
        paiement.setStatut(request.statut());
        paiement.setReference(request.reference());
        return PaiementMapper.toResponse(paiementRepository.save(paiement));
    }

    public void delete(String id) {
        Paiement paiement = getPaiement(id);
        if (paiement.getTransactionId() != null) {
            transactionRepository.findById(paiement.getTransactionId()).ifPresent(transaction -> {
                transaction.setPaiementId(null);
                transactionRepository.save(transaction);
            });
        }
        paiementRepository.delete(paiement);
    }

    public PaiementResponse linkTransaction(String paiementId, String transactionId) {
        Paiement paiement = getPaiement(paiementId);
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction introuvable avec l'ID : " + transactionId));

        if (paiement.getTransactionId() != null && !paiement.getTransactionId().equals(transactionId)) {
            transactionRepository.findById(paiement.getTransactionId()).ifPresent(previous -> {
                previous.setPaiementId(null);
                transactionRepository.save(previous);
            });
        }
        if (transaction.getPaiementId() != null && !transaction.getPaiementId().equals(paiementId)) {
            paiementRepository.findById(transaction.getPaiementId()).ifPresent(previous -> {
                previous.setTransactionId(null);
                paiementRepository.save(previous);
            });
        }

        paiement.setTransactionId(transactionId);
        transaction.setPaiementId(paiementId);
        paiementRepository.save(paiement);
        transactionRepository.save(transaction);
        return PaiementMapper.toResponse(paiement);
    }

    private Paiement getPaiement(String id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement introuvable avec l'ID : " + id));
    }
}