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
@RedisHash("Transaction")
public class Transaction implements Serializable {
    @Id
    private String id;
    private String reference;
    private LocalDateTime dateTransaction;
    private BigDecimal montant;
    private String statut;
    @Indexed
    private String paiementId;
}