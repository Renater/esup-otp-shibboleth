package fr.renater.shibboleth.idp.plugin.authn.esup.otp.dto;

import fr.renater.shibboleth.esup.otp.dto.EsupOtpWebauthnResponse;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.EsupOtpContext;
import lombok.Data;

import java.util.List;

/**
 * Webauthn Dto to transform {@link EsupOtpWebauthnResponse} from the API
 * to store into esup otp context {@link EsupOtpContext}.
 */
@Data
public class WebAuthnDto {

    private byte[] challenge;

    private Rp rp;

    private String rpId;

    private List<PubKeyCredParamsDto> pubKeyCredParams;

    private int timeout;

    private String attestation;

    private List<AllowCredentialDto> allowCredentials;

    @Data
    public static class Rp {

        private String name;

        private String id;
    }

    @Data
    public static class PubKeyCredParamsDto {

        private String type;

        private double alg;
    }

    @Data
    public static class AllowCredentialDto {

        private byte[] id;

        private String type;

        public AllowCredentialDto(byte[] id, String type) {
            this.id = id;
            this.type = type;
        }
    }
}
