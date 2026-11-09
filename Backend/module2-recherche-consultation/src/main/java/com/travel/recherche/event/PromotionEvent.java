package com.travel.recherche.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Événement Asynchrone RabbitMQ - Scénario 3 : Promotion Flash diffusée
 */
public class PromotionEvent implements Serializable {

    private Long voyageId;
    private String titre;
    private BigDecimal ancienPrix;
    private BigDecimal nouveauPrix;
    private Double pourcentageRemise;
    private LocalDateTime dateExpiration;

    public PromotionEvent() {
    }

    public PromotionEvent(Long voyageId, String titre, BigDecimal ancienPrix, BigDecimal nouveauPrix, Double pourcentageRemise, LocalDateTime dateExpiration) {
        this.voyageId = voyageId;
        this.titre = titre;
        this.ancienPrix = ancienPrix;
        this.nouveauPrix = nouveauPrix;
        this.pourcentageRemise = pourcentageRemise;
        this.dateExpiration = dateExpiration;
    }

    public Long getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(Long voyageId) {
        this.voyageId = voyageId;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public BigDecimal getAncienPrix() {
        return ancienPrix;
    }

    public void setAncienPrix(BigDecimal ancienPrix) {
        this.ancienPrix = ancienPrix;
    }

    public BigDecimal getNouveauPrix() {
        return nouveauPrix;
    }

    public void setNouveauPrix(BigDecimal nouveauPrix) {
        this.nouveauPrix = nouveauPrix;
    }

    public Double getPourcentageRemise() {
        return pourcentageRemise;
    }

    public void setPourcentageRemise(Double pourcentageRemise) {
        this.pourcentageRemise = pourcentageRemise;
    }

    public LocalDateTime getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDateTime dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    @Override
    public String toString() {
        return "PromotionEvent{" +
                "voyageId=" + voyageId +
                ", titre='" + titre + '\'' +
                ", ancienPrix=" + ancienPrix +
                ", nouveauPrix=" + nouveauPrix +
                ", remise=" + pourcentageRemise + "%" +
                '}';
    }
}
