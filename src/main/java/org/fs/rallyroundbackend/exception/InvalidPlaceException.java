package org.fs.rallyroundbackend.exception;

public class InvalidPlaceException extends RuntimeException {
    public InvalidPlaceException() {
        super("The provided place was not found");
    }
}
