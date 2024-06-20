package org.fs.rallyroundbackend.exception.auth;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class TermsAndConditionsException extends RallyRoundApiException {
    public TermsAndConditionsException() {
        super("The user must accept the terms and conditions of RallyRound to be able to enter.");
    }
}
