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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Esup otp webauthn response dto retruned by the API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EsupOtpWebauthnResponse extends EsupOtpResponse {

    private String nonce;

    private List<EsupOtpAuth> auths;

    @JsonProperty("user_id")
    private String userId;

    private EsupOtpRp rp;

    private List<PubKey> pubKeyTypes;

    @Data
    public static class EsupOtpAuth {

        @JsonProperty("credentialID")
        private String credentialId;

        private String credentialPublicKey;

        private int counter;

        private String name;

        @JsonProperty("_id")
        private String id;
    }

    @Data
    public static class EsupOtpRp {

        private String name;

        private String id;
    }

    @Data
    public static class PubKey {

        private String type;

        private double alg;
    }
}
