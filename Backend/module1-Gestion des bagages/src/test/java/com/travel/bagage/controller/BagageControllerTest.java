package com.travel.bagage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.bagage.dto.BagageRequest;
import com.travel.bagage.dto.BagageResponse;
import com.travel.bagage.exception.BaggageWeightExceededException;
import com.travel.bagage.exception.GlobalExceptionHandler;
import com.travel.bagage.exception.ResourceNotFoundException;
import com.travel.bagage.service.BagageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BagageController.class)
@Import(GlobalExceptionHandler.class)
class BagageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BagageService bagageService;

    @Test
    @DisplayName("POST /api/bagages - Devrait créer un bagage avec succès (201 Created)")
    void shouldCreateBagageSuccessfully() throws Exception {
        BagageRequest request = new BagageRequest();
        request.setType("CABINE");
        request.setPoids(15.0);
        request.setStatut("ENREGISTRE");
        request.setReservationId("RES-001");

        BagageResponse response = new BagageResponse();
        response.setId("BAG-001");
        response.setType("CABINE");
        response.setPoids(15.0);
        response.setStatut("ENREGISTRE");
        response.setPrix(50.0);
        response.setReservationId("RES-001");

        when(bagageService.createBagage(any(BagageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/bagages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("BAG-001"))
                .andExpect(jsonPath("$.prix").value(50.0));
    }

    @Test
    @DisplayName("POST /api/bagages - Devrait renvoyer 400 Bad Request si validation échoue (ex: poids négatif)")
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        BagageRequest invalidRequest = new BagageRequest();
        invalidRequest.setType(""); // NotBlank
        invalidRequest.setPoids(-5.0); // Positive
        invalidRequest.setStatut(""); // NotBlank

        mockMvc.perform(post("/api/bagages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/bagages - Devrait renvoyer 400 Bad Request quand BaggageWeightExceededException est levée")
    void shouldReturnBadRequestWhenWeightExceeded() throws Exception {
        BagageRequest request = new BagageRequest();
        request.setType("SOUTE");
        request.setPoids(35.0);
        request.setStatut("ENREGISTRE");
        request.setReservationId("RES-001");

        when(bagageService.createBagage(any(BagageRequest.class)))
                .thenThrow(new BaggageWeightExceededException("Bagage refusé : poids supérieur à 30 kg"));

        mockMvc.perform(post("/api/bagages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bagage refusé : poids supérieur à 30 kg"));
    }

    @Test
    @DisplayName("GET /api/bagages/{id} - Devrait renvoyer 404 Not Found si le bagage n'existe pas")
    void shouldReturnNotFoundWhenBagageDoesNotExist() throws Exception {
        when(bagageService.getBagageById("INCONNU"))
                .thenThrow(new ResourceNotFoundException("Bagage introuvable avec l'ID : INCONNU"));

        mockMvc.perform(get("/api/bagages/INCONNU"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Bagage introuvable avec l'ID : INCONNU"));
    }

    @Test
    @DisplayName("GET /api/bagages - Devrait renvoyer la liste des bagages")
    void shouldReturnAllBagages() throws Exception {
        BagageResponse r1 = new BagageResponse();
        r1.setId("B1");
        r1.setType("CABINE");
        r1.setPoids(8.0);
        r1.setPrix(30.0);

        when(bagageService.getAllBagages()).thenReturn(List.of(r1));

        mockMvc.perform(get("/api/bagages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("B1"));
    }

    @Test
    @DisplayName("PATCH /api/bagages/{id}/reservation/{reservationId} - Devrait associer un bagage à une réservation")
    void shouldAssociateBagageToReservation() throws Exception {
        BagageResponse response = new BagageResponse();
        response.setId("BAG-999");
        response.setReservationId("RES-888");
        response.setType("CABINE");
        response.setPoids(9.0);
        response.setPrix(30.0);
        response.setStatut("REGISTERED");

        when(bagageService.associateToReservation("BAG-999", "RES-888")).thenReturn(response);

        mockMvc.perform(patch("/api/bagages/BAG-999/reservation/RES-888"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("BAG-999"))
                .andExpect(jsonPath("$.reservationId").value("RES-888"));
    }

    @Test
    @DisplayName("GET /api/bagages/statistics - Devrait renvoyer les statistiques globales")
    void shouldReturnStatistics() throws Exception {
        com.travel.bagage.dto.BagageStatisticsResponse stats = com.travel.bagage.dto.BagageStatisticsResponse.builder()
                .totalBagages(3)
                .totalWeight(45.0)
                .totalRevenue(130.0)
                .averageWeight(15.0)
                .pendingBagages(1)
                .registeredBagages(2)
                .build();

        when(bagageService.getStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/bagages/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBagages").value(3))
                .andExpect(jsonPath("$.totalWeight").value(45.0))
                .andExpect(jsonPath("$.totalRevenue").value(130.0))
                .andExpect(jsonPath("$.averageWeight").value(15.0));
    }
}
