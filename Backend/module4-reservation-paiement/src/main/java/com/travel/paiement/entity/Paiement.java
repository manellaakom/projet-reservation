package com.travel.paiement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Paiement")
public class Paiement implements Serializable {
    @Id
    private String id;
    private BigDecimal montant;
    private LocalDateTime datePaiement;
    private String statut;
    private String reference;
    private Boolean paiementEnLigne;
    @Indexed
    private String paymeeToken;
    private String transactionId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Boolean getPaiementEnLigne() { return paiementEnLigne; }
    public void setPaiementEnLigne(Boolean paiementEnLigne) { this.paiementEnLigne = paiementEnLigne; }

    public String getPaymeeToken() { return paymeeToken; }
    public void setPaymeeToken(String paymeeToken) { this.paymeeToken = paymeeToken; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}