package com.travel.paiement.service;

public class InvalidPaymeeWebhookException extends RuntimeException {

    public InvalidPaymeeWebhookException(String message) {
        super(message);
    }
}
