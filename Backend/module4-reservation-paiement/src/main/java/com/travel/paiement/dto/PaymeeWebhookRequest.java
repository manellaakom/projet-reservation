package com.travel.paiement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymeeWebhookRequest(
        @NotBlank String token,
        @NotBlank @JsonProperty("check_sum") String checkSum,
        @NotNull @JsonProperty("payment_status") Boolean paymentStatus,
        @JsonProperty("transaction_id") String transactionId
) {
}
