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

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 */
public class EsupOtpUserInfoResponse extends EsupOtpResponse {

    private User user;
    
    public static class User {
        
        private Methods methods;
        
        private Transports transports;
        
        @JsonProperty("last_send_message")
        private LastSendMessage lastSendMessage;
        
        public static class Methods {
            
            private boolean codeRequired;
            
            private boolean waitingFor;
            
            private Active totp;
            
            @JsonProperty("random_code")
            private RandomCode randomCode;
            
            @JsonProperty("random_code_mail")
            private RandomCode randomCodeMail;
            
            
            public static class RandomCode extends Active {
                
                private List<String> transports;
            }
            
            public static class ByPass extends Active {
                
                @JsonProperty("available_code")
                private int availableCode;
            }
            
            public static class Active {
                
                private boolean active;
            }
        }
        
        public static class Transports {
            
            private String mail;
            
            private String sms;
            
            private String push;
        }
        
        public static class LastSendMessage {
            
            private String method;
            
            private LocalDateTime time;
            
            private boolean auto;
        }
    }
}
