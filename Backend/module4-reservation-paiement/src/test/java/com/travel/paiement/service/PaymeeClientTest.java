package com.travel.paiement.service;

import com.travel.paiement.config.PaymeeProperties;
import com.travel.paiement.dto.PaymeeCheckoutRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PaymeeClientTest {

    private static final String API_KEY = "test-api-key";

    private PaymeeClient client;
    private PaymeeProperties properties;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        properties = new PaymeeProperties();
        properties.setApiKey(API_KEY);
        properties.setBaseUrl("https://sandbox.paymee.tn");
        properties.setFrontendBaseUrl("https://frontend.example.test");
        properties.setWebhookUrl("https://api.example.test/api/paiements/paymee/webhook");

        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new PaymeeClient(builder, properties);
    }

    @Test
    void createsHostedCheckoutWithPaymeeFieldsAndTokenAuthorization() {
        server.expect(requestTo("https://sandbox.paymee.tn/api/v2/payments/create"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Token " + API_KEY))
                .andExpect(content().json("""
                        {
                          "amount": 12.50,
                          "note": "RES-001",
                          "first_name": "Amina",
                          "last_name": "Ben Ali",
                          "email": "amina@example.test",
                          "phone": "+21612345678",
                          "return_url": "https://frontend.example.test/?paiement=payment-id",
                          "cancel_url": "https://frontend.example.test/?paiementAnnule=payment-id",
                          "webhook_url": "https://api.example.test/api/paiements/paymee/webhook",
                          "order_id": "payment-id"
                        }
                        """))
                .andRespond(withSuccess("""
                        {
                          "status": true,
                          "data": {
                            "token": "paymee-token",
                            "payment_url": "https://sandbox.paymee.tn/gateway/paymee-token"
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        PaymeeClient.CheckoutSession checkout = client.createCheckout(
                new PaymeeCheckoutRequest(
                        new BigDecimal("12.50"),
                        "RES-001",
                        "Amina",
                        "Ben Ali",
                        "amina@example.test",
                        "+21612345678"
                ),
                "payment-id",
                "https://frontend.example.test/?paiement=payment-id",
                "https://frontend.example.test/?paiementAnnule=payment-id"
        );

        assertEquals("paymee-token", checkout.token());
        assertEquals("https://sandbox.paymee.tn/gateway/paymee-token", checkout.paymentUrl());
        server.verify();
    }

    @Test
    void checksPaymentStatusAndReturnsAmountAndTransactionId() {
        server.expect(requestTo("https://sandbox.paymee.tn/api/v2/payments/paymee-token/check"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Token " + API_KEY))
                .andRespond(withSuccess("""
                        {
                          "status": true,
                          "data": {
                            "payment_status": true,
                            "amount": 12.50,
                            "transaction_id": "transaction-001"
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        PaymeeClient.PaymentCheck check = client.checkPayment("paymee-token");

        assertTrue(check.paymentStatus());
        assertEquals(new BigDecimal("12.50"), check.amount());
        assertEquals("transaction-001", check.transactionId());
        server.verify();
    }

    @Test
    void validatesWebhookChecksumAgainstConfiguredApiKey() throws Exception {
        String signatureInput = "paymee-token1" + API_KEY;
        String expectedChecksum = java.util.HexFormat.of().formatHex(
                MessageDigest.getInstance("MD5").digest(signatureInput.getBytes(StandardCharsets.UTF_8))
        );

        assertTrue(client.isValidWebhookChecksum("paymee-token", true, expectedChecksum.toUpperCase()));
        assertFalse(client.isValidWebhookChecksum("paymee-token", false, expectedChecksum));
        assertFalse(client.isValidWebhookChecksum("other-token", true, expectedChecksum));
    }
}
