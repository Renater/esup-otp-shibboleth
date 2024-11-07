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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserMethods {

    private boolean codeRequired;
    
    private boolean waitingFor;
        
    private UserMethod totp;
    
    private UserMethod webauthn;
    
    @JsonProperty("random_code")
    private UserMethod randomCode;
    
    @JsonProperty("random_code_mail")
    private UserMethod randomCodeMail;
    
    private UserMethod bypass;
    
    private UserMethod push;
    
    private UserMethod esupnfc;
    
    @JsonIgnore
    public Map<String, UserMethod> getAll() {
        Map<String, UserMethod> userMethodByType = new HashMap<String, UserMethods.UserMethod>();
        userMethodByType.put("totp", totp);
        userMethodByType.put("webauthn", webauthn);
        userMethodByType.put("random_code", randomCode);
        userMethodByType.put("random_code_mail", randomCodeMail);
        userMethodByType.put("bypass", bypass);
        userMethodByType.put("push", push);
        userMethodByType.put("esupnfc", esupnfc);
        return userMethodByType;
    }
    
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserMethod {
    
        private boolean active;
        
        private List<String> transports;
        
        private String message;
        
        private String qrCode;
        
        private List<Integer> codes;
        
        @JsonProperty("available_code")
        private int availableCode;
        
        @JsonProperty("used_code")
        private int usedCode;
        
        private Device device;
        
    }
}
