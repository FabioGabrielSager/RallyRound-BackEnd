package org.fs.rallyroundbackend.exception.mercadopago;

import com.mercadopago.exceptions.MPMalformedRequestException;
import lombok.Getter;
import org.fs.rallyroundbackend.client.mercadopago.response.MPErrorResponse;

@Getter
public class MPAccessTokenRequestException extends MPMalformedRequestException {
    private final MPErrorResponse error;

    public MPAccessTokenRequestException(String message, MPErrorResponse error) {
        super(message);
        this.error = error;
    }
}
