package com.travel.bagage.exception;

/**
 * Exception levée lorsque le poids d'un bagage dépasse la limite autorisée.
 * Cette classe assure la compatibilité avec l'orthographe BagageWeightExceededException
 * et BaggageWeightExceededException.
 */
public class BagageWeightExceededException extends BaggageWeightExceededException {

    public BagageWeightExceededException(String message) {
        super(message);
    }

    public BagageWeightExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
