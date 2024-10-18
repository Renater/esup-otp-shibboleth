/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.renater.shibboleth.esup.otp.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.idp.authn.AbstractAuthenticationAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.AuthenticationErrorContext;
import net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import fr.renater.shibboleth.esup.otp.context.EsupOtpContext;
import jakarta.servlet.http.HttpServletRequest;

/**
/**
 * An action that derives a username from a lookup strategy, a TOTP code from an arbitrary source,
 * creates a {@link EsupOtpContext}, and attaches it to the {@link AuthenticationContext}.
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link AuthnEventIds#NO_CREDENTIALS}
 * @event {@link AuthnEventIds#UNKNOWN_USERNAME}
 * @event {@link AuthnEventIds#INVALID_CREDENTIALS}
 * @pre <pre>ProfileRequestContext.getSubcontext(AuthenticationContext.class) != null</pre>
 * @post <pre>AuthenticationContext.getSubcontext(TOTPContext.class) != null</pre>
 */
public class EsupOtpExtractionAction extends AbstractAuthenticationAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpExtractionAction.class);
    
    /** Lookup strategy for username to use in resolving token seeds. */
    @Nonnull private Function<ProfileRequestContext,String> usernameLookupStrategy;
    
    /** Creation strategy for TOTP context. */
    @Nonnull private Function<AuthenticationContext, EsupOtpContext> esupOtpContextCreationStrategy;
    
    /** Constructor. */
    public EsupOtpExtractionAction() {
        usernameLookupStrategy = new CanonicalUsernameLookupStrategy();
        esupOtpContextCreationStrategy = new ChildContextLookup<>(EsupOtpContext.class, true);
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
    public void setTOTPContextCreationStrategy(@Nonnull final Function<AuthenticationContext, EsupOtpContext> strategy) {
        checkSetterPreconditions();
        
        esupOtpContextCreationStrategy = Constraint.isNotNull(strategy, "TOTPContext creation strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext) {

        // Clear error state.
        authenticationContext.removeSubcontext(AuthenticationErrorContext.class);
        
        final EsupOtpContext totpContext = esupOtpContextCreationStrategy.apply(authenticationContext);
        if (totpContext == null) {
            log.warn("{} Unable to create TOTP context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return;
        }
        
        
        totpContext.setTokenCode(null);
        
        // Fill in username if not set.
        if (totpContext.getUsername() == null) {
            final String username = usernameLookupStrategy.apply(profileRequestContext);
            if (username == null) {
                log.warn("{} No principal name available", getLogPrefix());
                ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.UNKNOWN_USERNAME);
                return;
            }
            totpContext.setUsername(username);
        }
        
        final HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            log.debug("{} Profile action does not contain an HttpServletRequest", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }
        
        final String code = extractCode(request);
        if (code == null) {
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }
        
        try {
            totpContext.setTokenCode(Integer.valueOf(code));
        } catch (final NumberFormatException e) {
            log.warn("{} Exception converting code string to an integer", getLogPrefix(), e);
            authenticationContext.ensureSubcontext(AuthenticationErrorContext.class).getClassifiedErrors().add(
                    AuthnEventIds.INVALID_CREDENTIALS);
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.INVALID_CREDENTIALS);
        }
    }

    /**
     * Gets the token code from the HTTP request.
     * First get from form request (input "tokencode"),
     * Second get from header ("X-Shibboleth-ESUPOTP")
     * 
     * @param httpRequest current HTTP request
     * 
     * @return the token code, or null
     */
    @Nullable protected String extractCode(@Nonnull final HttpServletRequest httpRequest) {
        String code = httpRequest.getParameter("tokencode");
        if(code == null) {
            code = httpRequest.getHeader("X-Shibboleth-ESUPOTP");
        }
        
        return code;
    }
    
}