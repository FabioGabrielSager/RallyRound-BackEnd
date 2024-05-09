package org.fs.rallyroundbackend.controller;

import com.mercadopago.exceptions.MPMalformedRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.HmacUtils;
import org.fs.rallyroundbackend.dto.mercadopago.MpWebHookNotificationDto;
import org.fs.rallyroundbackend.exception.mercadopago.MPAccessTokenRequestException;
import org.fs.rallyroundbackend.exception.mercadopago.MPAccountAlreadyLinkedException;
import org.fs.rallyroundbackend.service.JwtService;
import org.fs.rallyroundbackend.service.MPAuthService;
import org.fs.rallyroundbackend.service.MPPaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/rr/api/v1/mp")
@RequiredArgsConstructor
public class MercadoPagoController {

    private final MPPaymentService mpPaymentService;
    private final MPAuthService mpAuthService;
    private final JwtService jwtService;
    @Value("${mp.hook.secret.key}")
    private String webHookKey = "";

    @GetMapping("/auth/url/")
    public String getMpAuthUrl(HttpServletRequest request) {
        String userEmail = jwtService.getUsernameFromToken(jwtService.getTokenFromRequest(request));
        try {
            return this.mpAuthService.getAuthenticationUrl(userEmail);
        } catch (MPAccountAlreadyLinkedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/auth/account/linked/")
    public ResponseEntity<Boolean> isAccountLinked(HttpServletRequest request) {
        return ResponseEntity.ok(this.mpAuthService.isAccountAlreadyLinked(
                this.jwtService.getUsernameFromToken(this.jwtService.getTokenFromRequest(request))));
    }

    @PostMapping("/auth/authorize/")
    public ResponseEntity<Boolean> linkAccount(@RequestParam(required = false) String code,
                                               @RequestParam(required = false) String state,
                                               HttpServletRequest request) {
        this.verifyMpSignature(request);

        try {
            this.mpAuthService.getAccessToken(code, UUID.fromString(state));
        } catch (MPAccessTokenRequestException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(e.getError().status()), e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment/done/")
    public void receivePaymentNotification(@RequestBody MpWebHookNotificationDto mpWebHookNotification,
                                           HttpServletRequest request) {

//        this.verifyMpSignature(request);

        if(mpWebHookNotification.getType() == null || !mpWebHookNotification.getType().equals("payment"))
            return;

        this.mpPaymentService.processEventInscriptionPaymentNotification(mpWebHookNotification);
    }

    private void verifyMpSignature(HttpServletRequest request) {
        try {
            String xSignature = request.getHeader("x-signature");
            String xRequestId = request.getHeader("x-request-id");
            String dataId = request.getParameter("data.id");

            String ts = "";
            String hash = "";

            for (String var : xSignature.split(",")) {
                String[] keyValue = var.split("=");
                if(keyValue[0].equals("ts")) {
                    ts = keyValue[1];
                } else if(keyValue[0].equals("v1")) {
                    hash = keyValue[1];
                }
            }

            String manifest = String.format("id:%s;request-id:%s;ts:%s;", dataId, xRequestId, ts);

            String encryptedSignature = new HmacUtils("HmacSHA256", webHookKey).hmacHex(manifest);

            if(!Objects.equals(hash, encryptedSignature)) {
                throw new MPMalformedRequestException("Signature verification failed");
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Signature verification failed");
        }
    }
}
