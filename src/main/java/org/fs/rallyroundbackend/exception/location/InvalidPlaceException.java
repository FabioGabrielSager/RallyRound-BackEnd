package org.fs.rallyroundbackend.exception.location;

public class InvalidPlaceException extends RuntimeException {
    public InvalidPlaceException() {
        super("The provided place was not found");
    }
}
