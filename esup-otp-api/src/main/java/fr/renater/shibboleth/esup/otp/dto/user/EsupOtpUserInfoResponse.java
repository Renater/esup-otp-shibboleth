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

package fr.renater.shibboleth.esup.otp.dto.user;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;
import lombok.Data;

/**
 * Esup otp user response.
 */
@Data
public class EsupOtpUserInfoResponse extends EsupOtpResponse {

    /** user description. */
    private User user;

    @Data
    public static class User {
        
        private UserMethods methods;
        
        private Transports transports;
        
        @JsonProperty("last_send_message")
        private LastSendMessage lastSendMessage;
        
        @Data
        public static class Transports {
            
            private String mail;
            
            private String sms;
            
            private String push;

            @JsonIgnore
            public Map<String, String> getAll() {
                Map<String, String> transportByType = new HashMap<String, String>();
                transportByType.put("mail", mail);
                transportByType.put("sms", sms);
                transportByType.put("push", push);
                return transportByType;
            }
            
        }
        
        @Data
        public static class LastSendMessage {
            
            private String method;
            
            private Instant time;
            
            private boolean auto;
        }
    }
}
