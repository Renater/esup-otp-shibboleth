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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Esup otp webauthn response dto retruned by the API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper = true)
public class EsupOtpWebauthnResponse extends EsupOtpResponse {

    /**
     * Nonce value get from esup-otp-api, need to be send to api on validation,
     * in {@link EsupOtpClient#postVerifyWebauthn(String, EsupOtpVerifyWebAuthnRequest)}.
     */
    private String nonce;

    /**
     * List of authentication already registered.
     */
    private List<EsupOtpAuth> auths;

    /** User uid. */
    @JsonProperty("user_id")
    private String userId;

    /**
     * Relying party manage the authentication.
     */
    private EsupOtpRp rp;

    /**
     * List of public key types.
     *
     * @see <a href="https://www.iana.org/assignments/cose/cose.xhtml#algorithms">COSE Algorithms</a>
     */
    private List<PubKey> pubKeyTypes;

    /**
     * Attested credential data is a variable-length byte array added to the authenticator data when
     * generating an attestation object for a given credential. This class provides access to the three
     * data segments of that byte array.
     *
     * @see <a
     *     href="https://www.w3.org/TR/2021/REC-webauthn-2-20210408/#sctn-attested-credential-data">6.4.1.
     *     Attested Credential Data</a>
     */
    @Data
    public static class EsupOtpAuth {

        /** The credential's credential ID for the public key in base64. */
        @JsonProperty("credentialID")
        private String credentialId;

        /** The credential's public key in base64. */
        private String credentialPublicKey;

        /** The number of times the authenticator reported it has been used. */
        private int counter;

        /** Credential name. */
        private String name;

        /*@JsonProperty("_id")
        private String id;*/
    }

    /**
     * Relying party object.
     */
    @Data
    public static class EsupOtpRp {

        /** Name of relying party. */
        private String name;

        /** Id of the relying party that manages the authentication. The id is a domain. */
        private String id;
    }

    /**
     * Used to supply additional parameters when creating a new credential.
     *
     * @see <a
     *     href="https://www.w3.org/TR/2021/REC-webauthn-2-20210408/#dictdef-publickeycredentialparameters">§5.3.
     *     Parameters for Credential Generation (dictionary PublicKeyCredentialParameters) </a>
     */
    @Data
    public static class PubKey {

        /** Specifies the type of credential. */
        private String type;

        /**
         * Specifies the cryptographic signature algorithm with which the newly generated credential will
         * be used, and thus also the type of asymmetric key pair to be generated, e.g., RSA or Elliptic
         * Curve.
         */
        private double alg;
    }
}
