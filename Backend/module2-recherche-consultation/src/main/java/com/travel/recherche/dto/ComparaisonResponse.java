package com.travel.recherche.dto;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    private List<VoyageResponse> offresComparees = new ArrayList<>();


    // --- Constructors ---
    public ComparaisonResponse() {}

    public ComparaisonResponse(Integer nombreOffresComparees, Long offreMoinsChereId, String offreMoinsChereTitre, BigDecimal meilleurPrix, Long offrePlusRapideId, String offrePlusRapideTitre, Integer dureeMinimaleMinutes, Long offreMieuxNoteeId, String offreMieuxNoteeTitre, Double meilleureNote, Long offreMeilleurRapportQualitePrixId, String offreMeilleurRapportQualitePrixTitre, Integer meilleurScoreQualitePrix, BigDecimal ecartPrixMax, Integer ecartDureeMinutes, String syntheseRecommandation, List<VoyageResponse> offresComparees) {
        this.nombreOffresComparees = nombreOffresComparees;
        this.offreMoinsChereId = offreMoinsChereId;
        this.offreMoinsChereTitre = offreMoinsChereTitre;
        this.meilleurPrix = meilleurPrix;
        this.offrePlusRapideId = offrePlusRapideId;
        this.offrePlusRapideTitre = offrePlusRapideTitre;
        this.dureeMinimaleMinutes = dureeMinimaleMinutes;
        this.offreMieuxNoteeId = offreMieuxNoteeId;
        this.offreMieuxNoteeTitre = offreMieuxNoteeTitre;
        this.meilleureNote = meilleureNote;
        this.offreMeilleurRapportQualitePrixId = offreMeilleurRapportQualitePrixId;
        this.offreMeilleurRapportQualitePrixTitre = offreMeilleurRapportQualitePrixTitre;
        this.meilleurScoreQualitePrix = meilleurScoreQualitePrix;
        this.ecartPrixMax = ecartPrixMax;
        this.ecartDureeMinutes = ecartDureeMinutes;
        this.syntheseRecommandation = syntheseRecommandation;
        this.offresComparees = offresComparees;
    }

    // --- Getters & Setters ---
    public Integer getNombreOffresComparees() {
        return this.nombreOffresComparees;
    }
    public void setNombreOffresComparees(Integer nombreOffresComparees) {
        this.nombreOffresComparees = nombreOffresComparees;
    }
    public Long getOffreMoinsChereId() {
        return this.offreMoinsChereId;
    }
    public void setOffreMoinsChereId(Long offreMoinsChereId) {
        this.offreMoinsChereId = offreMoinsChereId;
    }
    public String getOffreMoinsChereTitre() {
        return this.offreMoinsChereTitre;
    }
    public void setOffreMoinsChereTitre(String offreMoinsChereTitre) {
        this.offreMoinsChereTitre = offreMoinsChereTitre;
    }
    public BigDecimal getMeilleurPrix() {
        return this.meilleurPrix;
    }
    public void setMeilleurPrix(BigDecimal meilleurPrix) {
        this.meilleurPrix = meilleurPrix;
    }
    public Long getOffrePlusRapideId() {
        return this.offrePlusRapideId;
    }
    public void setOffrePlusRapideId(Long offrePlusRapideId) {
        this.offrePlusRapideId = offrePlusRapideId;
    }
    public String getOffrePlusRapideTitre() {
        return this.offrePlusRapideTitre;
    }
    public void setOffrePlusRapideTitre(String offrePlusRapideTitre) {
        this.offrePlusRapideTitre = offrePlusRapideTitre;
    }
    public Integer getDureeMinimaleMinutes() {
        return this.dureeMinimaleMinutes;
    }
    public void setDureeMinimaleMinutes(Integer dureeMinimaleMinutes) {
        this.dureeMinimaleMinutes = dureeMinimaleMinutes;
    }
    public Long getOffreMieuxNoteeId() {
        return this.offreMieuxNoteeId;
    }
    public void setOffreMieuxNoteeId(Long offreMieuxNoteeId) {
        this.offreMieuxNoteeId = offreMieuxNoteeId;
    }
    public String getOffreMieuxNoteeTitre() {
        return this.offreMieuxNoteeTitre;
    }
    public void setOffreMieuxNoteeTitre(String offreMieuxNoteeTitre) {
        this.offreMieuxNoteeTitre = offreMieuxNoteeTitre;
    }
    public Double getMeilleureNote() {
        return this.meilleureNote;
    }
    public void setMeilleureNote(Double meilleureNote) {
        this.meilleureNote = meilleureNote;
    }
    public Long getOffreMeilleurRapportQualitePrixId() {
        return this.offreMeilleurRapportQualitePrixId;
    }
    public void setOffreMeilleurRapportQualitePrixId(Long offreMeilleurRapportQualitePrixId) {
        this.offreMeilleurRapportQualitePrixId = offreMeilleurRapportQualitePrixId;
    }
    public String getOffreMeilleurRapportQualitePrixTitre() {
        return this.offreMeilleurRapportQualitePrixTitre;
    }
    public void setOffreMeilleurRapportQualitePrixTitre(String offreMeilleurRapportQualitePrixTitre) {
        this.offreMeilleurRapportQualitePrixTitre = offreMeilleurRapportQualitePrixTitre;
    }
    public Integer getMeilleurScoreQualitePrix() {
        return this.meilleurScoreQualitePrix;
    }
    public void setMeilleurScoreQualitePrix(Integer meilleurScoreQualitePrix) {
        this.meilleurScoreQualitePrix = meilleurScoreQualitePrix;
    }
    public BigDecimal getEcartPrixMax() {
        return this.ecartPrixMax;
    }
    public void setEcartPrixMax(BigDecimal ecartPrixMax) {
        this.ecartPrixMax = ecartPrixMax;
    }
    public Integer getEcartDureeMinutes() {
        return this.ecartDureeMinutes;
    }
    public void setEcartDureeMinutes(Integer ecartDureeMinutes) {
        this.ecartDureeMinutes = ecartDureeMinutes;
    }
    public String getSyntheseRecommandation() {
        return this.syntheseRecommandation;
    }
    public void setSyntheseRecommandation(String syntheseRecommandation) {
        this.syntheseRecommandation = syntheseRecommandation;
    }
    public List<VoyageResponse> getOffresComparees() {
        return this.offresComparees;
    }
    public void setOffresComparees(List<VoyageResponse> offresComparees) {
        this.offresComparees = offresComparees;
    }

    // --- Builder Pattern ---
    public static ComparaisonResponseBuilder builder() {
        return new ComparaisonResponseBuilder();
    }

    public static class ComparaisonResponseBuilder {
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
        private List<VoyageResponse> offresComparees = new ArrayList<>();

        public ComparaisonResponseBuilder() {}

        public ComparaisonResponseBuilder nombreOffresComparees(Integer nombreOffresComparees) {
            this.nombreOffresComparees = nombreOffresComparees;
            return this;
        }
        public ComparaisonResponseBuilder offreMoinsChereId(Long offreMoinsChereId) {
            this.offreMoinsChereId = offreMoinsChereId;
            return this;
        }
        public ComparaisonResponseBuilder offreMoinsChereTitre(String offreMoinsChereTitre) {
            this.offreMoinsChereTitre = offreMoinsChereTitre;
            return this;
        }
        public ComparaisonResponseBuilder meilleurPrix(BigDecimal meilleurPrix) {
            this.meilleurPrix = meilleurPrix;
            return this;
        }
        public ComparaisonResponseBuilder offrePlusRapideId(Long offrePlusRapideId) {
            this.offrePlusRapideId = offrePlusRapideId;
            return this;
        }
        public ComparaisonResponseBuilder offrePlusRapideTitre(String offrePlusRapideTitre) {
            this.offrePlusRapideTitre = offrePlusRapideTitre;
            return this;
        }
        public ComparaisonResponseBuilder dureeMinimaleMinutes(Integer dureeMinimaleMinutes) {
            this.dureeMinimaleMinutes = dureeMinimaleMinutes;
            return this;
        }
        public ComparaisonResponseBuilder offreMieuxNoteeId(Long offreMieuxNoteeId) {
            this.offreMieuxNoteeId = offreMieuxNoteeId;
            return this;
        }
        public ComparaisonResponseBuilder offreMieuxNoteeTitre(String offreMieuxNoteeTitre) {
            this.offreMieuxNoteeTitre = offreMieuxNoteeTitre;
            return this;
        }
        public ComparaisonResponseBuilder meilleureNote(Double meilleureNote) {
            this.meilleureNote = meilleureNote;
            return this;
        }
        public ComparaisonResponseBuilder offreMeilleurRapportQualitePrixId(Long offreMeilleurRapportQualitePrixId) {
            this.offreMeilleurRapportQualitePrixId = offreMeilleurRapportQualitePrixId;
            return this;
        }
        public ComparaisonResponseBuilder offreMeilleurRapportQualitePrixTitre(String offreMeilleurRapportQualitePrixTitre) {
            this.offreMeilleurRapportQualitePrixTitre = offreMeilleurRapportQualitePrixTitre;
            return this;
        }
        public ComparaisonResponseBuilder meilleurScoreQualitePrix(Integer meilleurScoreQualitePrix) {
            this.meilleurScoreQualitePrix = meilleurScoreQualitePrix;
            return this;
        }
        public ComparaisonResponseBuilder ecartPrixMax(BigDecimal ecartPrixMax) {
            this.ecartPrixMax = ecartPrixMax;
            return this;
        }
        public ComparaisonResponseBuilder ecartDureeMinutes(Integer ecartDureeMinutes) {
            this.ecartDureeMinutes = ecartDureeMinutes;
            return this;
        }
        public ComparaisonResponseBuilder syntheseRecommandation(String syntheseRecommandation) {
            this.syntheseRecommandation = syntheseRecommandation;
            return this;
        }
        public ComparaisonResponseBuilder offresComparees(List<VoyageResponse> offresComparees) {
            this.offresComparees = offresComparees;
            return this;
        }

        public ComparaisonResponse build() {
            ComparaisonResponse instance = new ComparaisonResponse();
            instance.nombreOffresComparees = this.nombreOffresComparees;
            instance.offreMoinsChereId = this.offreMoinsChereId;
            instance.offreMoinsChereTitre = this.offreMoinsChereTitre;
            instance.meilleurPrix = this.meilleurPrix;
            instance.offrePlusRapideId = this.offrePlusRapideId;
            instance.offrePlusRapideTitre = this.offrePlusRapideTitre;
            instance.dureeMinimaleMinutes = this.dureeMinimaleMinutes;
            instance.offreMieuxNoteeId = this.offreMieuxNoteeId;
            instance.offreMieuxNoteeTitre = this.offreMieuxNoteeTitre;
            instance.meilleureNote = this.meilleureNote;
            instance.offreMeilleurRapportQualitePrixId = this.offreMeilleurRapportQualitePrixId;
            instance.offreMeilleurRapportQualitePrixTitre = this.offreMeilleurRapportQualitePrixTitre;
            instance.meilleurScoreQualitePrix = this.meilleurScoreQualitePrix;
            instance.ecartPrixMax = this.ecartPrixMax;
            instance.ecartDureeMinutes = this.ecartDureeMinutes;
            instance.syntheseRecommandation = this.syntheseRecommandation;
            instance.offresComparees = this.offresComparees;
            return instance;
        }
    }

}

