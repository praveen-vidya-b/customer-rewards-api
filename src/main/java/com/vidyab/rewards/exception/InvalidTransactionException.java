package com.vidyab.rewards.exception;

/**
 * Thrown when transaction data is invalid.
 */
public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String message) {
        super(message);
    }
}