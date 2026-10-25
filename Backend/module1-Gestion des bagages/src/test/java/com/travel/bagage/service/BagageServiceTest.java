package com.travel.bagage.service;

import com.travel.bagage.dto.BagageRequest;
import com.travel.bagage.dto.BagageResponse;
import com.travel.bagage.dto.BagageStatisticsResponse;
import com.travel.bagage.entity.Bagage;
import com.travel.bagage.exception.BaggageWeightExceededException;
import com.travel.bagage.exception.ResourceNotFoundException;
import com.travel.bagage.mapper.BagageMapper;
import com.travel.bagage.repository.BagageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BagageServiceTest {

    @Mock
    private BagageRepository bagageRepository;

    private BagageMapper bagageMapper;
    private BagageService bagageService;

    @BeforeEach
    void setUp() {
        bagageMapper = new BagageMapper();
        bagageService = new BagageService(bagageRepository, bagageMapper);
    }

    @Test
    @DisplayName("Devrait calculer le prix correct selon le poids")
    void shouldCalculateCorrectPrice() {
        assertEquals(30.0, bagageService.calculatePrice(5.0));
        assertEquals(30.0, bagageService.calculatePrice(10.0));
        assertEquals(50.0, bagageService.calculatePrice(15.0));
        assertEquals(50.0, bagageService.calculatePrice(20.0));
        assertEquals(70.0, bagageService.calculatePrice(25.0));
        assertEquals(70.0, bagageService.calculatePrice(30.0));
    }

    @Test
    @DisplayName("Devrait lever une exception si le poids dépasse 30 kg ou est négatif")
    void shouldThrowWhenWeightInvalid() {
        assertThrows(BaggageWeightExceededException.class, () -> bagageService.validateWeight(35.0));
        assertThrows(BaggageWeightExceededException.class, () -> bagageService.validateWeight(-1.0));
        assertThrows(BaggageWeightExceededException.class, () -> bagageService.validateWeight(0.0));
    }

    @Test
    @DisplayName("Devrait créer un bagage avec calcul automatique du prix")
    void shouldCreateBagage() {
        BagageRequest request = new BagageRequest();
        request.setType("CABINE");
        request.setPoids(12.0);
        request.setStatut("ENREGISTRE");
        request.setReservationId("RES-123");

        when(bagageRepository.save(any(Bagage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BagageResponse response = bagageService.createBagage(request);

        assertNotNull(response);
        assertEquals(12.0, response.getPoids());
        assertEquals(50.0, response.getPrix());
        assertEquals("RES-123", response.getReservationId());
        verify(bagageRepository, times(1)).save(any(Bagage.class));
    }

    @Test
    @DisplayName("Devrait récupérer un bagage existant par son ID")
    void shouldGetBagageById() {
        Bagage bagage = new Bagage("BAG-1", "RES-123", "SOUTE", 22.0, "VALIDE", 70.0);
        when(bagageRepository.findById("BAG-1")).thenReturn(Optional.of(bagage));

        BagageResponse response = bagageService.getBagageById("BAG-1");

        assertNotNull(response);
        assertEquals("BAG-1", response.getId());
        assertEquals(22.0, response.getPoids());
    }

    @Test
    @DisplayName("Devrait lever ResourceNotFoundException pour un ID inexistant")
    void shouldThrowWhenBagageNotFound() {
        when(bagageRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bagageService.getBagageById("UNKNOWN"));
    }

    @Test
    @DisplayName("Devrait supprimer un bagage existant")
    void shouldDeleteBagage() {
        Bagage bagage = new Bagage("BAG-1", "RES-123", "SOUTE", 22.0, "VALIDE", 70.0);
        when(bagageRepository.findById("BAG-1")).thenReturn(Optional.of(bagage));

        bagageService.deleteBagage("BAG-1");

        verify(bagageRepository, times(1)).delete(bagage);
    }

    @Test
    @DisplayName("Devrait associer un bagage existant à une nouvelle réservation")
    void shouldAssociateToReservation() {
        Bagage bagage = new Bagage("BAG-1", "OLD-RES", "SOUTE", 22.0, "VALIDE", 70.0);
        when(bagageRepository.findById("BAG-1")).thenReturn(Optional.of(bagage));
        when(bagageRepository.save(any(Bagage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BagageResponse response = bagageService.associateToReservation("BAG-1", "NEW-RES-456");

        assertNotNull(response);
        assertEquals("BAG-1", response.getId());
        assertEquals("NEW-RES-456", response.getReservationId());
        verify(bagageRepository, times(1)).save(bagage);
    }

    @Test
    @DisplayName("Devrait calculer les statistiques globales correctement")
    void shouldCalculateStatistics() {
        List<Bagage> list = List.of(
                new Bagage("1", "R1", "CABINE", 10.0, "ENREGISTRE", 30.0),
                new Bagage("2", "R2", "SOUTE", 20.0, "EN ATTENTE", 50.0)
        );
        when(bagageRepository.findAll()).thenReturn(list);

        BagageStatisticsResponse stats = bagageService.getStatistics();

        assertEquals(2, stats.getTotalBagages());
        assertEquals(30.0, stats.getTotalWeight());
        assertEquals(80.0, stats.getTotalRevenue());
        assertEquals(15.0, stats.getAverageWeight());
        assertEquals(1, stats.getPendingBagages());
        assertEquals(1, stats.getRegisteredBagages());
    }
}
