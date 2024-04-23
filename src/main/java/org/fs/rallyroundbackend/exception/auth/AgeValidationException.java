package org.fs.rallyroundbackend.exception.auth;

public class AgeValidationException extends RuntimeException {
    public AgeValidationException(String message) {
        super(message);
    }
}
