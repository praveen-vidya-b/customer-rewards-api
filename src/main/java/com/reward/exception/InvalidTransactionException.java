package com.reward.exception;

/**
 * Thrown when transaction data is invalid.
 */
public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String message) {
        super(message);
    }
}