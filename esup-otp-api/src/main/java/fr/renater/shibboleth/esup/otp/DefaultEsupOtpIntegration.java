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

package fr.renater.shibboleth.esup.otp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.security.auth.Subject;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

import java.security.Principal;
import java.util.Collection; 
import java.util.Set;

/**
 * Wrapper for use of esup otp api.
 */
@ThreadSafe
public final class DefaultEsupOtpIntegration extends AbstractInitializableComponent implements IEsupOtpIntegration { 
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DefaultEsupOtpIntegration.class);
    
    /** API host. */
    @GuardedBy("this") @NonnullAfterInit @NotEmpty private String apiHost; 

    /** Integration key. */
    @GuardedBy("this") @Nullable private String clientId;
    
    /** Secret key. */ 
    @GuardedBy("this") @Nullable private String secretKey;
    
    /** Api password .*/
    @GuardedBy("this") @Nullable private String apiPassword;
    
    /** The used (by clients) redirect_uri to send the client after authorisation .*/
    @GuardedBy("this") @Nullable private String redirectURI;
    
    /** A statically set (pre-registered) redirectURI to send the client to after authorisation.*/
    @GuardedBy("this") @Nullable private String registeredRedirectURI;
    
    /** The URL path to the health endpoint.*/
    @GuardedBy("this") @Nullable private String healthEndpoint;

    @GuardedBy("this") @Nonnull private final Subject supportedPrincipals;

    /**
     * Constructor.
     *
     */
    public DefaultEsupOtpIntegration() {
        supportedPrincipals = new Subject();
    }
    
    /** {@inheritDoc} */
    @Nonnull @NotEmpty public synchronized String getAPIHost() {
        checkComponentActive();
        assert apiHost != null;
        return apiHost;
    }

    /**
     * Set the API host to use.
     * 
     * @param host API host
     */
    public synchronized void setAPIHost(@Nonnull @NotEmpty final String host) {
        checkSetterPreconditions();        
        apiHost = Constraint.isNotNull(StringSupport.trimOrNull(host), "API host cannot be null or empty");
    }
    
    /**
     * Set the client ID to use.
     * 
     * @param id the client identifier.
     */
    public synchronized void setClientId(@Nullable final String id) {
        checkSetterPreconditions();        
        clientId = Constraint.isNotNull(StringSupport.trimOrNull(id), "ClientID cannot be null or empty");
    }
    
    /** {@inheritDoc} */
    @Nullable public synchronized String getClientId() {
        checkComponentActive();
        assert clientId != null;
        return clientId;
    }
    
    /**
     * Set the secret key to use.
     * 
     * @param key secret key
     */
    public synchronized void setSecretKey(@Nullable final String key) {
        checkSetterPreconditions();        
        secretKey = Constraint.isNotNull(StringSupport.trimOrNull(key), "Secret key cannot be null or empty");
    }
    
    /** {@inheritDoc} */
    @Nullable public synchronized String getSecretKey() {
        checkComponentActive();
        assert secretKey != null;
        return secretKey;
    }
    
    /**
     * Set the api password to use.
     * 
     * @param apiPwd secret key
     */
    public synchronized void setApiPassword(@Nullable final String apiPwd) {
        checkSetterPreconditions();        
        apiPassword = StringSupport.trimOrNull(apiPwd);
    }
    
    /** {@inheritDoc} */
    @Nullable public synchronized String getApiPassword() {
        return apiPassword;
    }
    
    
    /** {@inheritDoc} */
    @Nullable public synchronized String getRedirectURI() {
        return redirectURI;
    }
    
    /**
     * Set the redirect_uri to use.
     * 
     * @param uri the redirect_uri
     */
    public synchronized void setRegisteredRedirectURI(@Nullable final String uri) {
        checkSetterPreconditions();       
        registeredRedirectURI = StringSupport.trimOrNull(uri);
    }
    
    /** {@inheritDoc} */
    @Nullable public synchronized String getRegisteredRedirectURI() {
        return registeredRedirectURI;
    }
    
    /** {@inheritDoc} */
    @Nullable public synchronized String getHealthCheckEndpoint() {
        checkComponentActive();
        assert healthEndpoint != null;
        return healthEndpoint;
    }
    
    /**
     * Set the health check endpoint URL path.
     * 
     * @param endpoint the endpoint.
     */
    public synchronized void setHealthCheckEndpoint(@Nullable final String endpoint) {
        checkSetterPreconditions();        
        healthEndpoint = Constraint.isNotNull(StringSupport.trimOrNull(endpoint), 
                "Health check endpoint cannot be null or empty");
    }
    
    /** {@inheritDoc} */
    public synchronized void setRedirectURIIfAbsent(
            @Nonnull @NotEmpty final String computedRedirectURI){   
        // Specifically do not check if component has been initialized. This can change during use.
        Constraint.isNotEmpty(computedRedirectURI, "Computed redirect URI can not be null or empty");
        
        if (redirectURI == null) {
            log.debug("Integration redirect_uri is being pinned to '{}'",computedRedirectURI);
            redirectURI = computedRedirectURI;
        }
    }

    @Nonnull
    @Override
    public <T extends Principal> Set<T> getSupportedPrincipals(@Nonnull Class<T> c) {
        final Set<T> result = supportedPrincipals.getPrincipals(c);
        assert result != null;
        return result;
    }

    /**
     * Set supported non-user-specific principals that the action will include in the subjects
     * it generates, in place of any default principals from the flow.
     *
     * <p>Setting to a null or empty collection will maintain the default behavior of relying on the flow.</p>
     *
     * @param <T> a type of principal to add, if not generic
     * @param principals supported principals to include
     */
    public synchronized <T extends Principal> void setSupportedPrincipals(
            @Nullable @NonnullElements final Collection<T> principals) {
        checkSetterPreconditions();
        supportedPrincipals.getPrincipals().clear();

        if (principals != null && !principals.isEmpty()) {
            supportedPrincipals.getPrincipals().addAll(Set.copyOf(principals));
        }
    }
}
