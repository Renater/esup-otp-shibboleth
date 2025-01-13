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

package fr.renater.shibboleth.idp.plugin.authn.esup.otp.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import fr.renater.shibboleth.idp.plugin.authn.esup.otp.dto.WebAuthnDto;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.dto.WebAuthnPublicKeyCredential;
import org.opensaml.messaging.context.BaseContext;

import com.google.common.base.Strings;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

import java.util.*;

/**
 * Context class for state of a Esup otp validation.
 */
public class EsupOtpContext extends BaseContext {
    
    /** The subject identifier with respect to the token "back-end". */
    @Nullable @NotEmpty private String username;

    /** The choices configured by user". */
    @Nullable private Set<String> enabledChoices;

    /** The transports configured by user". */
    @Nullable private Map<String, String> configuredTransports;

    /** The token code supplied. */
    @Nullable private String transportChoose;

    private int sendCounter;

    /** The webauthn dto. */
    @Nullable private WebAuthnDto webauthnCredentialRequestOptions;

    /** A public key credential with assertion response that is the result of authentication.*/
    @Nullable private WebAuthnPublicKeyCredential publicKeyCredentialAssertionResponse;

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
     * Get the enabledChoices.
     *
     * @return the enabledChoices
     */
    public @Nullable List<String> getEnabledChoices() {
        return enabledChoices != null ? enabledChoices.stream().toList() : new ArrayList<String>();
    }

    /**
     * Set the enabledChoices.
     *
     * @param choices the possible choices for user
     *
     * @return this context
     */
    @Nonnull public EsupOtpContext setEnabledChoices(@Nullable final Set<String> choices) {
        if(choices == null) {
            enabledChoices = new HashSet<>();
        } else {
            enabledChoices = choices;
        }

        return this;
    }

    /**
     * Get the configured transports.
     *
     * @return the configuredTransports
     */
    @Nullable public Map<String, String> getConfiguredTransports() {
        return configuredTransports;
    }

    /**
     * Set the configuredTransports.
     *
     * @param transportsByType the possible transports for user
     *
     * @return this context
     */
    @Nonnull public EsupOtpContext setConfiguredTransports(@Nullable final Map<String, String> transportsByType) {
        if(transportsByType == null) {
            configuredTransports = new HashMap<>();
        } else {
            configuredTransports = transportsByType;
        }

        return this;
    }

    /**
     * Get the transport choose.
     *
     * @return the transport choose (method.transport)
     */
    @Nullable public WebAuthnDto getWebauthnCredentialRequestOptions() {
        return webauthnCredentialRequestOptions;
    }

    /**
     * Set the webauthn values.
     *
     * @param webauthndto the webauthn dto.
     *
     * @return this context
     */
    @Nonnull public EsupOtpContext setWebauthnCredentialRequestOptions(@Nullable final WebAuthnDto webauthndto) {
        webauthnCredentialRequestOptions = webauthndto;

        return this;
    }

    /**
     * Get the transport choose.
     *
     * @return the transport choose (method.transport)
     */
    @Nullable public String getTransportChoose() {
        return transportChoose;
    }

    /**
     * Set the transport choose.
     *
     * @param methodAndTransport the transport choose (method.transport)
     *
     * @return this context
     */
    @Nonnull public EsupOtpContext setTransportChoose(@Nullable final String methodAndTransport) {
        transportChoose = methodAndTransport;

        return this;
    }

    /**
     * Get counter of sent.
     *
     * @return the sendCounter.
     */
    @Nonnull public int getSendCounter() {
        return sendCounter;
    }

    /**
     * Set the counter of sent.
     *
     * @param counter the counter of send message done.
     *
     * @return this context
     */
    @Nonnull public EsupOtpContext setSendCounter(@Nonnull final int counter) {
        sendCounter = counter;

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

    @Nonnull public EsupOtpContext setPublicKeyCredentialAssertionResponse(@Nullable final WebAuthnPublicKeyCredential pkCredAssertion) {
        this.publicKeyCredentialAssertionResponse = pkCredAssertion;

        return this;
    }

    @Nullable public WebAuthnPublicKeyCredential getPublicKeyCredentialAssertionResponse() {
        return publicKeyCredentialAssertionResponse;
    }
}
