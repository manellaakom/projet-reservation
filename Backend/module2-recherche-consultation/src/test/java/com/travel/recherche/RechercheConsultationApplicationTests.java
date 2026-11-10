package com.travel.recherche;

import com.travel.recherche.dto.ComparaisonResponse;
import com.travel.recherche.dto.CritereRechercheRequest;
import com.travel.recherche.dto.SuggestionsResponse;
import com.travel.recherche.dto.VoyageResponse;
import com.travel.recherche.enums.TypeTransport;
import com.travel.recherche.service.RechercheConsultationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RechercheConsultationApplicationTests {

    @Autowired
    private RechercheConsultationService service;

    @Test
    void contextLoadsAndCatalogueIsSeeded() {
        List<VoyageResponse> voyages = service.rechercherEtFiltrerVoyages(new CritereRechercheRequest());
        assertFalse(voyages.isEmpty(), "Le catalogue initial doit contenir des voyages");
    }

    @Test
    void testFiltrageEtTriParPrix() {
        CritereRechercheRequest criteres = CritereRechercheRequest.builder()
                .villeDestination("Paris")
                .typeTransport(TypeTransport.VOL)
                .prixMax(BigDecimal.valueOf(500))
                .sortBy("prix")
                .sortDir("asc")
                .build();

        List<VoyageResponse> resultats = service.rechercherEtFiltrerVoyages(criteres);
        assertEquals(2, resultats.size());
        assertTrue(resultats.get(0).getPrixEffectif().compareTo(resultats.get(1).getPrixEffectif()) <= 0);
    }

    @Test
    void testComparaisonOffres() {
        ComparaisonResponse comparaison = service.comparerOffres(List.of(1L, 2L, 3L));
        assertEquals(3, comparaison.getNombreOffresComparees());
        assertNotNull(comparaison.getOffreMoinsChereId());
        assertNotNull(comparaison.getOffreMeilleurRapportQualitePrixId());
        assertNotNull(comparaison.getSyntheseRecommandation());
    }

    @Test
    void testSuggestionsPourOffre() {
        SuggestionsResponse suggestions = service.obtenirSuggestionsPourOffre(1L);
        assertNotNull(suggestions);
        assertFalse(suggestions.getOffresSimilaires().isEmpty());
    }
}
