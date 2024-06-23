package org.fs.rallyroundbackend.exception.event;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class DisabledActivityException extends RallyRoundApiException {
    public DisabledActivityException() {
        super("The selected activity is not enabled.");
    }
}
