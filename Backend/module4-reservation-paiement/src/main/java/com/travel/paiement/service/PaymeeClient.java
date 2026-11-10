package com.travel.paiement.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.travel.paiement.config.PaymeeProperties;
import com.travel.paiement.dto.PaymeeCheckoutRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class PaymeeClient {

    private final RestClient restClient;
    private final PaymeeProperties properties;

    public PaymeeClient(RestClient.Builder restClientBuilder, PaymeeProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    public CheckoutSession createCheckout(
            PaymeeCheckoutRequest request,
            String orderId,
            String returnUrl,
            String cancelUrl
    ) {
        validateConfiguration();

        CreatePaymentPayload payload = new CreatePaymentPayload(
                request.montant(),
                request.reference(),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone(),
                returnUrl,
                cancelUrl,
                properties.getWebhookUrl(),
                orderId
        );

        try {
            ApiResponse response = restClient.post()
                    .uri(properties.getBaseUrl() + "/api/v2/payments/create")
                    .header(HttpHeaders.AUTHORIZATION, "Token " + properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(ApiResponse.class);

            if (response == null || !Boolean.TRUE.equals(response.status())
                    || response.data() == null
                    || isBlank(response.data().token())
                    || !isPaymeeCheckoutUrl(response.data().paymentUrl())) {
                throw new PaymeeIntegrationException("Paymee n'a pas pu créer la session de paiement.");
            }

            return new CheckoutSession(response.data().token(), response.data().paymentUrl());
        } catch (RestClientException exception) {
            throw new PaymeeIntegrationException("Le service Paymee est momentanément indisponible.", exception);
        }
    }

    public PaymentCheck checkPayment(String token) {
        validateApiKey();
        URI checkUri = UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .pathSegment("api", "v2", "payments", token, "check")
                .build()
                .encode()
                .toUri();

        try {
            ApiResponse response = restClient.get()
                    .uri(checkUri)
                    .header(HttpHeaders.AUTHORIZATION, "Token " + properties.getApiKey())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(ApiResponse.class);

            if (response == null || !Boolean.TRUE.equals(response.status())
                    || response.data() == null || response.data().paymentStatus() == null) {
                throw new PaymeeIntegrationException("Paymee n'a pas retourné de statut de paiement vérifiable.");
            }
            return new PaymentCheck(
                    response.data().paymentStatus(),
                    response.data().amount(),
                    response.data().transactionId()
            );
        } catch (RestClientException exception) {
            throw new PaymeeIntegrationException("Impossible de vérifier le paiement auprès de Paymee.", exception);
        }
    }

    public boolean isValidWebhookChecksum(String token, boolean paymentStatus, String checkSum) {
        if (isBlank(token) || isBlank(checkSum) || isBlank(properties.getApiKey())) {
            return false;
        }

        String status = paymentStatus ? "1" : "0";
        String expected = md5(token + status + properties.getApiKey());
        return MessageDigest.isEqual(
                expected.getBytes(java.nio.charset.StandardCharsets.US_ASCII),
                checkSum.trim().toLowerCase(java.util.Locale.ROOT)
                        .getBytes(java.nio.charset.StandardCharsets.US_ASCII)
        );
    }

    public void validateConfiguration() {
        validateApiKey();
        requireHttpsUrl(properties.getFrontendBaseUrl(), "PAYMEE_FRONTEND_BASE_URL");
        requireHttpsUrl(properties.getWebhookUrl(), "PAYMEE_WEBHOOK_URL");
        requireHttpsUrl(properties.getBaseUrl(), "PAYMEE_BASE_URL");
    }

    private void validateApiKey() {
        if (isBlank(properties.getApiKey())) {
            throw new PaymeeConfigurationException(
                    "Le paiement en ligne n'est pas configuré. Définissez PAYMEE_API_KEY côté backend."
            );
        }
    }

    private void requireHttpsUrl(String value, String propertyName) {
        if (isBlank(value)) {
            throw new PaymeeConfigurationException(propertyName + " doit être configurée côté backend.");
        }
        URI uri;
        try {
            uri = URI.create(value);
        } catch (IllegalArgumentException exception) {
            throw new PaymeeConfigurationException(propertyName + " doit être une URL HTTPS valide.");
        }
        if (properties.isMock() && "http".equalsIgnoreCase(uri.getScheme()) && !isBlank(uri.getHost())) {
            return;
        }
        if (!"https".equalsIgnoreCase(uri.getScheme()) || isBlank(uri.getHost())) {
            throw new PaymeeConfigurationException(propertyName + " doit être une URL HTTPS valide.");
        }
    }

    private boolean isPaymeeCheckoutUrl(String paymentUrl) {
        if (isBlank(paymentUrl)) {
            return false;
        }
        if (properties.isMock()) {
            return paymentUrl.startsWith(properties.getBaseUrl());
        }
        try {
            URI checkoutUri = URI.create(paymentUrl);
            String host = checkoutUri.getHost();
            return "https".equalsIgnoreCase(checkoutUri.getScheme())
                    && host != null
                    && (host.equalsIgnoreCase("paymee.tn") || host.toLowerCase(java.util.Locale.ROOT).endsWith(".paymee.tn"));
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private String md5(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5")
                    .digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("MD5 is not available", exception);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record CheckoutSession(String token, String paymentUrl) {
    }

    public record PaymentCheck(
            Boolean paymentStatus,
            java.math.BigDecimal amount,
            String transactionId
    ) {
    }

    private record CreatePaymentPayload(
            java.math.BigDecimal amount,
            String note,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName,
            String email,
            String phone,
            @JsonProperty("return_url") String returnUrl,
            @JsonProperty("cancel_url") String cancelUrl,
            @JsonProperty("webhook_url") String webhookUrl,
            @JsonProperty("order_id") String orderId
    ) {
    }

    private record ApiResponse(Boolean status, String message, Integer code, ApiPaymentData data) {
    }

    private record ApiPaymentData(
            String token,
            @JsonProperty("payment_url") String paymentUrl,
            @JsonProperty("payment_status") Boolean paymentStatus,
            java.math.BigDecimal amount,
            @JsonProperty("transaction_id") String transactionId
    ) {
    }
}
