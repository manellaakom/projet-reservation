package com.travel.bagage.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto implements Serializable {
    private String id;
    private String codeReservation;
    private String voyageurNom;
    private String volNumero;
    private String statut;
    private boolean valide;
    private String message;
}
