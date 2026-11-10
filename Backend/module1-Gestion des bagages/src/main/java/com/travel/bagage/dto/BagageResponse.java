package com.travel.bagage.dto;

import lombok.Data;

@Data
public class BagageResponse {
    private String id;
    private String reservationId;
    private String type;
    private Double poids;
    private String statut;
    private Double prix;
}
