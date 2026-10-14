package com.travel.bagage.exception;

public class BaggageWeightExceededException extends RuntimeException {
    public BaggageWeightExceededException(String message) {
        super(message);
    }
}
