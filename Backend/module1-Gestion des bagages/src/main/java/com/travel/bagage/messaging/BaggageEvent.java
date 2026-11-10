package com.travel.bagage.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaggageEvent implements Serializable {
    private String eventType; // BAGAGE_CREATED, BAGAGE_UPDATED, RESERVATION_CANCELLED
    private String bagageId;
    private String reservationId;
    private Double poids;
    private Double prix;
    private String statut;
    private String details;
    private LocalDateTime timestamp;
}
