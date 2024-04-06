package org.fs.rallyroundbackend.exception;

/**
 * Exception that is thrown if the email verification process fails
 * */
public class UnsuccefulyEmailVerificationException extends RuntimeException {
    public UnsuccefulyEmailVerificationException() {
        super("Error: Couldn't verify email");
    }
}
