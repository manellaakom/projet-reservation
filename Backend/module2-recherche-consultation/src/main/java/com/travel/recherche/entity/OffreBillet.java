package com.travel.recherche.entity;

import com.travel.recherche.enums.ClasseBillet;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "offres_billets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OffreBillet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codeOffreBillet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClasseBillet classe;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(nullable = false)
    private Integer placesRestantes;

    @Column(nullable = false)
    @Builder.Default
    private Integer bagageCabineKg = 8;

    @Column(nullable = false)
    @Builder.Default
    private Integer bagageSouteKg = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean modifiable = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean remboursable = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean choixSiegeGratuit = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accesSalonVip = false;

    @Column(length = 500)
    private String conditionsTarifaires;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voyage_id", nullable = false)
    private Voyage voyage;
}
