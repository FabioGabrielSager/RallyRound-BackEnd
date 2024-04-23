package org.fs.rallyroundbackend.exception.location;

public class InvalidAddressException extends RuntimeException {
    public InvalidAddressException() {
        super("The provided address was not found");
    }
}
