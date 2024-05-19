package org.fs.rallyroundbackend.exception.report;

import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;

public class ReportsLimitException extends RallyRoundApiException {
    public ReportsLimitException(String message) {
        super(message);
    }
}
