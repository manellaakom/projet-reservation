package com.travel.paiement.service;

public class PaymeeIntegrationException extends RuntimeException {

    public PaymeeIntegrationException(String message) {
        super(message);
    }

    public PaymeeIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
