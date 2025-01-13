package fr.renater.shibboleth.idp.plugin.authn.esup.otp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebAuthnPublicKeyCredential {

    private String id;

    private String type;

    private byte[] rawId;

    private ClientAssertionExtensionOutputs clientExtensionResults;

    private WebAuthnAuthenticatorAssertionResponse response;

    private String authenticatorAttachment;

    @Data
    public static class WebAuthnAuthenticatorAssertionResponse {

        private byte[] authenticatorData;

        @JsonProperty("clientDataJSON")
        private byte[] clientDataJson;

        private byte[] signature;

        private byte[] userHandle;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientAssertionExtensionOutputs {

        private boolean appid;

        private LargeBlobAuthenticationOutput largeBlob;

        @Data
        public static class LargeBlobAuthenticationOutput {

            private byte[] blob;

            private Boolean written;
        }
    }
}
