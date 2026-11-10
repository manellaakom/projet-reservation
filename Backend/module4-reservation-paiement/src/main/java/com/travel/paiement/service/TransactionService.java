package com.travel.paiement.service;

import com.travel.paiement.dto.TransactionRequest;
import com.travel.paiement.dto.TransactionResponse;
import com.travel.paiement.entity.Paiement;
import com.travel.paiement.entity.Transaction;
import com.travel.paiement.exception.ResourceNotFoundException;
import com.travel.paiement.mapper.TransactionMapper;
import com.travel.paiement.repository.PaiementRepository;
import com.travel.paiement.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaiementRepository paiementRepository;

    public TransactionService(TransactionRepository transactionRepository, PaiementRepository paiementRepository) {
        this.transactionRepository = transactionRepository;
        this.paiementRepository = paiementRepository;
    }

    public TransactionResponse create(TransactionRequest request) {
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), request.reference(),
                request.dateTransaction(), request.montant(), request.statut(), null);
        return TransactionMapper.toResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> findAll() {
        List<TransactionResponse> result = new ArrayList<>();
        transactionRepository.findAll().forEach(transaction -> result.add(TransactionMapper.toResponse(transaction)));
        return result;
    }

    public TransactionResponse findById(String id) {
        return TransactionMapper.toResponse(getTransaction(id));
    }

    public TransactionResponse update(String id, TransactionRequest request) {
        Transaction transaction = getTransaction(id);
        transaction.setReference(request.reference());
        transaction.setDateTransaction(request.dateTransaction());
        transaction.setMontant(request.montant());
        transaction.setStatut(request.statut());
        return TransactionMapper.toResponse(transactionRepository.save(transaction));
    }

    public void delete(String id) {
        Transaction transaction = getTransaction(id);
        if (transaction.getPaiementId() != null) {
            paiementRepository.findById(transaction.getPaiementId()).ifPresent(paiement -> {
                paiement.setTransactionId(null);
                paiementRepository.save(paiement);
            });
        }
        transactionRepository.delete(transaction);
    }

    private Transaction getTransaction(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction introuvable avec l'ID : " + id));
    }
}