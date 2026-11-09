package com.travel.recherche.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Événement Asynchrone RabbitMQ - Scénario 2 : Paiement validé avec succès
 */
public class PaiementEvent implements Serializable {

    private String paiementId;
    private Long voyageId;
    private BigDecimal montant;
    private String modePaiement;
    private String statut;
    private LocalDateTime datePaiement;

    public PaiementEvent() {
    }

    public PaiementEvent(String paiementId, Long voyageId, BigDecimal montant, String modePaiement, String statut, LocalDateTime datePaiement) {
        this.paiementId = paiementId;
        this.voyageId = voyageId;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.statut = statut;
        this.datePaiement = datePaiement;
    }

    public String getPaiementId() {
        return paiementId;
    }

    public void setPaiementId(String paiementId) {
        this.paiementId = paiementId;
    }

    public Long getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(Long voyageId) {
        this.voyageId = voyageId;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    @Override
    public String toString() {
        return "PaiementEvent{" +
                "paiementId='" + paiementId + '\'' +
                ", voyageId=" + voyageId +
                ", montant=" + montant +
                ", mode='" + modePaiement + '\'' +
                ", statut='" + statut + '\'' +
                ", datePaiement=" + datePaiement +
                '}';
    }
}
