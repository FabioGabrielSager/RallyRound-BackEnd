package org.fs.rallyroundbackend.client.mercadopago.response;

public record MPErrorResponse(String message, String error, int status, Object[] cause) {
}
