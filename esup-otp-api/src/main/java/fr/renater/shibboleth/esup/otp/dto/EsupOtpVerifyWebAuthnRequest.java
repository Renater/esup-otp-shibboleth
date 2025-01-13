/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.renater.shibboleth.esup.otp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.stream.Stream;

/**
 * Esup otp verify dto request for webauthn transport.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EsupOtpVerifyWebAuthnRequest {

    /** Webauthn response. */
    private WebAuthnResponse response;

    /** Credential id. */
    @JsonProperty("credID")
    private String credId;

    /**
     * Nested object of webauthn response.
     */
    @Data
    public static class WebAuthnResponse {

        /**
         * The raw Credential ID of this credential, corresponding to the <code>rawId</code> attribute in
         * the WebAuthn API.
         */
        private String id;

        /** The type value is the string "public-key". */
        private String type;

        /** Webauthn rawId. */
        private String rawId;

        /**
         * The authenticator's response to the client’s request to either create a public key credential,
         * or generate an authentication assertion.
         *
         * <p>The {@link WebAuthnResponse} was created in
         * response to <code>navigator.credentials.get()</code>, and this attribute’s value will be an
         * {@link ResponseData}.
         * </p>
         */
        private ResponseData response;

        private AuthenticatorAttachment authenticatorAttachment;

        /**
         * Represents an authenticator's response to a client’s request for generation of a new
         * authentication assertion given the WebAuthn Relying Party's {@linkplain
         * EsupOtpWebauthnResponse#getNonce()} challenge} and OPTIONAL {@linkplain
         * EsupOtpWebauthnResponse#getAuths()} list of credentials} it is aware of.
         * This response contains a cryptographic {@linkplain #signature} proving possession of the
         * credential private key, and optionally evidence of user consent to a specific transaction.
         *
         * @see <a
         *     href="https://www.w3.org/TR/2021/REC-webauthn-2-20210408/#authenticatorassertionresponse">§5.2.2.
         *     Web Authentication Assertion (interface AuthenticatorAssertionResponse) </a>
         */
        @Data
        public static class ResponseData {

            /** The authenticator data returned by the authenticator. */
            private String authenticatorData;

            /** The JSON-serialized client data passed to the authenticator 
             * by the client in the call to either navigator.credentials.create() or navigator.credentials.get(). 
             * The exact JSON serialization MUST be preserved, 
             * as the hash of the serialized client data has been computed. */
            @JsonProperty("clientDataJSON")
            private String clientDataJson;

            /**
             * The raw signature returned from the authenticator. See <a
             * href="https://www.w3.org/TR/2021/REC-webauthn-2-20210408/#sctn-op-get-assertion">§6.3.3 The
             * authenticatorGetAssertion Operation</a>.
             */
            private String signature;

            /**
             * The user handle returned from the authenticator, or empty if the authenticator did not return a
             * user handle. See <a
             * href="https://www.w3.org/TR/2021/REC-webauthn-2-20210408/#sctn-op-get-assertion">§6.3.3 The
             * authenticatorGetAssertion Operation</a>.
             */
            private String userHandle;
        }

        @AllArgsConstructor
        public enum AuthenticatorAttachment {
            /**
             * Indicates <a
             * href="https://www.w3.org/TR/2021/REC-webauthn-2-20210408/#cross-platform-attachment">cross-platform
             * attachment</a>.
             *
             * <p>Authenticators of this class are removable from, and can "roam" among, client platforms.
             */
            CROSS_PLATFORM("cross-platform"),

            /**
             * Indicates <a
             * href="https://www.w3.org/TR/2021/REC-webauthn-2-20210408/#platform-attachment">platform
             * attachment</a>.
             *
             * <p>Usually, authenticators of this class are not removable from the platform.
             */
            PLATFORM("platform");

            private final String value;

            @JsonCreator
            public static AuthenticatorAttachment fromString(String value) {
                return value != null ? Stream.of(values()).filter(v -> v.value.equals(value)).findAny().orElse(null) : null;
            }



        }
    }

}
