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

package fr.renater.shibboleth.esup.otp.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;

import com.google.common.base.Strings;

import fr.renater.shibboleth.esup.otp.connector.EsupOtpConnector;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Context class for state of a Esup otp validation.
 */
public class EsupOtpContext extends BaseContext {
    
    private EsupOtpConnector client;
    
    /** The subject identifier with respect to the token "back-end". */
    @Nullable @NotEmpty private String username;

    /** The token code supplied. */
    @Nullable private Integer tokenCode;
    
    /**
     * Get the username.
     *
     * @return the username
     */
    @Nullable @NotEmpty public String getUsername() {
        return username;
    }

    /**
     * Set the username.
     *
     * @param name the username
     *
     * @return this context
     */
    @Nonnull public EsupOtpContext setUsername(@Nullable @NotEmpty final String name) {
        if (Strings.isNullOrEmpty(name)) {
            username = null;
        } else {
            username = name;
        }
        
        return this;
    }

    /**
     * Get the token code.
     *
     * @return the token code
     */
    @Nullable public Integer getTokenCode() {
        return tokenCode;
    }

    /**
     * Set the token code.
     *
     * @param code the token code
     *
     * @return this context
     */
    @Nonnull public EsupOtpContext setTokenCode(@Nullable final Integer code) {
        tokenCode = code;
        
        return this;
    }

    /**
     * @return Returns the client.
     */
    public EsupOtpConnector getClient() {
        return client;
    }

    /**
     * @param client The client to set.
     */
    public void setClient(EsupOtpConnector client) {
        this.client = client;
    }

}
