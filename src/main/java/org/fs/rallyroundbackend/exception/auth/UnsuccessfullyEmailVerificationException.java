package org.fs.rallyroundbackend.exception.auth;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

/**
 * Exception that is thrown if the email verification process fails.
 */
public class UnsuccessfullyEmailVerificationException extends RallyRoundApiException {
    public UnsuccessfullyEmailVerificationException() {
        super("Error: Couldn't verify email");
    }
}
