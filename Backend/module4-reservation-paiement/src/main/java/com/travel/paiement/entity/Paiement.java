package com.travel.paiement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

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
    @Indexed
    private String transactionId;
}