package org.fs.rallyroundbackend.exception.auth;

/**
 * Exception that is thrown if the email verification process fails.
 */
public class UnsuccessfullyEmailVerificationException extends RuntimeException {
    public UnsuccessfullyEmailVerificationException() {
        super("Error: Couldn't verify email");
    }
}
