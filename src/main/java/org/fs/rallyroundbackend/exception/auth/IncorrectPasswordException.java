package org.fs.rallyroundbackend.exception.auth;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class IncorrectPasswordException extends RallyRoundApiException {
    public IncorrectPasswordException() {
        super("Incorrect password");
    }
}
