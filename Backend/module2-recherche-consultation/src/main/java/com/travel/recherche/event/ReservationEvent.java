package com.travel.recherche.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Événement Asynchrone RabbitMQ - Scénario 1 : Nouvelle Réservation créée
 */
public class ReservationEvent implements Serializable {

    private Long voyageId;
    private int nombrePlacesReservees;
    private String clientEmail;
    private LocalDateTime dateCreation;

    public ReservationEvent() {
    }

    public ReservationEvent(Long voyageId, int nombrePlacesReservees, String clientEmail, LocalDateTime dateCreation) {
        this.voyageId = voyageId;
        this.nombrePlacesReservees = nombrePlacesReservees;
        this.clientEmail = clientEmail;
        this.dateCreation = dateCreation;
    }

    public Long getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(Long voyageId) {
        this.voyageId = voyageId;
    }

    public int getNombrePlacesReservees() {
        return nombrePlacesReservees;
    }

    public void setNombrePlacesReservees(int nombrePlacesReservees) {
        this.nombrePlacesReservees = nombrePlacesReservees;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "ReservationEvent{" +
                "voyageId=" + voyageId +
                ", nombrePlaces=" + nombrePlacesReservees +
                ", clientEmail='" + clientEmail + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
