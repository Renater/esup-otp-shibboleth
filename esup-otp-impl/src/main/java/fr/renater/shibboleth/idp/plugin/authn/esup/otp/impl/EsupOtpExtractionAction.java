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

package fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl;

import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import fr.renater.shibboleth.esup.otp.DefaultEsupOtpIntegration;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClientException;
import net.shibboleth.idp.authn.AbstractAuthenticationAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.AuthenticationErrorContext;
import net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.EsupOtpContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * An action that derives a username from a lookup strategy, get otp code from form or header,
 * creates a {@link EsupOtpContext}, and attaches it to the {@link AuthenticationContext}.
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link AuthnEventIds#NO_CREDENTIALS}
 * @event {@link AuthnEventIds#UNKNOWN_USERNAME}
 * @event {@link AuthnEventIds#INVALID_CREDENTIALS}
 * @pre <pre>ProfileRequestContext.getSubcontext(AuthenticationContext.class) != null</pre>
 * @post <pre>AuthenticationContext.getSubcontext(EsupOtpContext.class) != null</pre>
 */
public class EsupOtpExtractionAction extends AbstractAuthenticationAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpExtractionAction.class);

    private static final String CLIENT_EXCEPTION = "ClientException";
    
    /** Lookup strategy for username to use in resolving token seeds. */
    @Nonnull private Function<ProfileRequestContext, String> usernameLookupStrategy;
    
    /** Creation strategy for esup otp context. */
    @Nonnull private Function<AuthenticationContext, EsupOtpContext> esupOtpContextCreationStrategy;

    /** Lookup strategy for esup otp integration. */
    @Nonnull private Function<ProfileRequestContext, DefaultEsupOtpIntegration> esupOtpIntegrationLookupStrategy;

    /** The registry for locating the EsupOtpClient for the established integration.*/
    @NonnullAfterInit
    private EsupOtpClientRegistry clientRegistry;
    
    /** Constructor. */
    public EsupOtpExtractionAction() {
        usernameLookupStrategy = new CanonicalUsernameLookupStrategy();
        esupOtpContextCreationStrategy = new ChildContextLookup<>(EsupOtpContext.class, true);

        esupOtpIntegrationLookupStrategy = FunctionSupport.constant(null);
    }

    /**
     * Set the EsupOtp client registry.
     *
     * @param esupOtpClientRegistry the registry
     */
    public void setClientRegistry(@Nonnull final EsupOtpClientRegistry esupOtpClientRegistry) {
        checkSetterPreconditions();

        clientRegistry = Constraint.isNotNull(esupOtpClientRegistry,"EsupOtpClient registry can not be null");
    }

    /**
     * Set the lookup strategy to use for the username to use in resolving token seeds.
     * 
     * @param strategy lookup strategy
     */
    public void setUsernameLookupStrategy(@Nonnull final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();
        
        usernameLookupStrategy = Constraint.isNotNull(strategy, "Username lookup strategy cannot be null");
    }

    /**
     * Set the lookup strategy to locate/create the {@link EsupOtpContext}.
     * 
     * @param strategy lookup/creation strategy
     */
    public void setEsupOtpContextCreationStrategy(
            @Nonnull final Function<AuthenticationContext, EsupOtpContext> strategy) {
        checkSetterPreconditions();
        
        esupOtpContextCreationStrategy = Constraint.isNotNull(strategy, 
                "EsupOtpContext creation strategy cannot be null");
    }

    /**
     * Set the lookup strategy to locate/create the {@link EsupOtpContext}.
     *
     * @param strategy lookup/creation strategy
     */
    public void setEsupOtpIntegrationLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, DefaultEsupOtpIntegration> strategy) {
        checkSetterPreconditions();

        esupOtpIntegrationLookupStrategy = Constraint.isNotNull(strategy, 
                "EsupOtpIntegration creation strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (clientRegistry ==  null) {
            throw new ComponentInitializationException("EsupOtp Client Registry cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext) {

        // Clear error state.
        authenticationContext.removeSubcontext(AuthenticationErrorContext.class);
        
        final EsupOtpContext esupOtpContext = esupOtpContextCreationStrategy.apply(authenticationContext);
        if (esupOtpContext == null) {
            log.warn("{} Unable to create esup otp context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return;
        }

        final DefaultEsupOtpIntegration esupOtpIntegration = esupOtpIntegrationLookupStrategy.apply(profileRequestContext);
        if (esupOtpIntegration == null) {
            log.warn("{} No EsupOtpIntegration returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return;
        }

        final EsupOtpClient client = clientRegistry.getClientOrCreate(esupOtpIntegration);
        
        esupOtpContext.setTokenCode(null);
        
        // Fill in username if not set.
        if (esupOtpContext.getUsername() == null) {
            final String username = usernameLookupStrategy.apply(profileRequestContext);
            if (username == null) {
                log.warn("{} No principal name available", getLogPrefix());
                ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.UNKNOWN_USERNAME);
                return;
            }
            esupOtpContext.setUsername(username);
        }
        
        final HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            log.debug("{} Profile action does not contain an HttpServletRequest", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }
        
        final String transport = extractTransport(request);
        if (transport == null) {
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }
        log.debug("Transport choose : {}", transport);
        esupOtpContext.setTransportChoose(transport);
        
        Map<String, String> configuredTransports = esupOtpContext.getConfiguredTransports();
        if(configuredTransports == null || configuredTransports.isEmpty()) {
            log.debug("{} Profile action does not contain configured transports", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }
        
        // String transportTo = configuredTransports.get(transport);

        try {
            if("push".equals(transport)) {
                client.postSendMessage(esupOtpContext.getUsername(), transport, transport);
            } else {
                String[] method_transport = transport.split("\\.");
                client.postSendMessage(esupOtpContext.getUsername(), method_transport[0], method_transport[1]);
            }

        } catch (EsupOtpClientException e) {
            log.info("{} Send message with option '{}' to '{}' failed", getLogPrefix(), transport, esupOtpContext.getUsername(), e);
            authenticationContext.ensureSubcontext(AuthenticationErrorContext.class).getClassifiedErrors().add(
                    CLIENT_EXCEPTION);
            ActionSupport.buildEvent(profileRequestContext, CLIENT_EXCEPTION);
        }
    }

    /**
     * Gets the transport choose from the HTTP request.
     * First get from form request (input "transportchoose"),
     * 
     * @param httpRequest current HTTP request
     * 
     * @return the token code, or null
     */
    @Nullable protected String extractTransport(@Nonnull final HttpServletRequest httpRequest) {
        return httpRequest.getParameter("transportchoose");
    }
    
}