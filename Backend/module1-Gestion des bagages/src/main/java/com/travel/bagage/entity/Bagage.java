package com.travel.bagage.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Bagage")
public class Bagage implements Serializable {
    @Id
    private String id;
    @Indexed
    private String reservationId;
    private String type;
    private Double poids;
    private String statut;
    private Double prix;
}
