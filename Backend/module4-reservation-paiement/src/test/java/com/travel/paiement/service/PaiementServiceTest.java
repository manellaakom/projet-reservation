package com.travel.paiement.service;

import com.travel.paiement.config.PaymeeProperties;
import com.travel.paiement.entity.Paiement;
import com.travel.paiement.repository.PaiementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaiementServiceTest {

    @Mock
    private PaiementRepository paiementRepository;
    @Mock
    private PaymeeClient paymeeClient;
    @Mock
    private PaymeeProperties paymeeProperties;
    @InjectMocks
    private PaiementService paiementService;

    @Test
    void refusesToConfirmPaymentWhenPaymeeAmountDoesNotMatch() {
        Paiement paiement = new Paiement();
        paiement.setId("payment-id");
        paiement.setMontant(new BigDecimal("12.50"));
        paiement.setStatut("En attente");
        paiement.setPaiementEnLigne(true);
        paiement.setPaymeeToken("paymee-token");
        when(paiementRepository.findById("payment-id")).thenReturn(Optional.of(paiement));
        when(paymeeClient.checkPayment("paymee-token"))
                .thenReturn(new PaymeeClient.PaymentCheck(true, new BigDecimal("12.49"), "transaction-001"));

        PaymeeIntegrationException exception = assertThrows(
                PaymeeIntegrationException.class,
                () -> paiementService.verifyPaymeePayment("payment-id")
        );

        assertTrue(exception.getMessage().contains("montant"));
        assertEquals("En attente", paiement.getStatut());
        verify(paiementRepository, never()).save(any(Paiement.class));
    }

    @Test
    void rejectsManualModificationOfPaymeePayments() {
        Paiement paiement = new Paiement();
        paiement.setId("payment-id");
        paiement.setPaiementEnLigne(true);
        when(paiementRepository.findById("payment-id")).thenReturn(Optional.of(paiement));

        assertThrows(IllegalStateException.class, () -> paiementService.update("payment-id", null));
        assertThrows(IllegalStateException.class, () -> paiementService.delete("payment-id"));
        verify(paiementRepository, never()).save(any(Paiement.class));
        verify(paiementRepository, never()).delete(any(Paiement.class));
    }
}
