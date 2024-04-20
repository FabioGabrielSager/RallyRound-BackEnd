package org.fs.rallyroundbackend.exception.mercadopago;

import com.mercadopago.exceptions.MPException;

public class MPAccountAlreadyLinkedException extends MPException {
    public MPAccountAlreadyLinkedException() {
        super("User has already authorized the link between their account and the app.");
    }
}
