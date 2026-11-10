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
public class PaymentDto implements Serializable {
    private String paymentId;
    private String bagageId;
    private String reservationId;
    private Double montant;
    private String devise;
    private String statut;
    private String message;
}
