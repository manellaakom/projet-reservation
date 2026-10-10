package com.travel.recherche.entity;

import com.travel.recherche.enums.ClasseBillet;
import com.travel.recherche.enums.TypeTransport;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiques_recherches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueRecherche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String motCle;
    private String villeDepart;
    private String villeDestination;

    @Enumerated(EnumType.STRING)
    private TypeTransport typeTransport;

    @Enumerated(EnumType.STRING)
    private ClasseBillet classeBillet;

    @Column(precision = 10, scale = 2)
    private BigDecimal budgetMax;

    private Integer nombreResultats;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateRecherche = LocalDateTime.now();
}
