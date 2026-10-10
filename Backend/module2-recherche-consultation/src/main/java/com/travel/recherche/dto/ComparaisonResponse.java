package com.travel.recherche.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComparaisonResponse {

    private Integer nombreOffresComparees;

    private Long offreMoinsChereId;
    private String offreMoinsChereTitre;
    private BigDecimal meilleurPrix;

    private Long offrePlusRapideId;
    private String offrePlusRapideTitre;
    private Integer dureeMinimaleMinutes;

    private Long offreMieuxNoteeId;
    private String offreMieuxNoteeTitre;
    private Double meilleureNote;

    private Long offreMeilleurRapportQualitePrixId;
    private String offreMeilleurRapportQualitePrixTitre;
    private Integer meilleurScoreQualitePrix;

    private BigDecimal ecartPrixMax;
    private Integer ecartDureeMinutes;
    private String syntheseRecommandation;

    @Builder.Default
    private List<VoyageResponse> offresComparees = new ArrayList<>();
}
