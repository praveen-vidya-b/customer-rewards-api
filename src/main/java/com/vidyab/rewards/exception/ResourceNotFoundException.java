package com.vidyab.rewards.exception;

/**
 * Thrown when the requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}