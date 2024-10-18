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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import fr.renater.shibboleth.esup.otp.context.EsupOtpContext;
import net.shibboleth.idp.authn.AbstractCredentialValidator;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.CredentialValidator;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.SubjectCanonicalizationContext;
import net.shibboleth.idp.authn.principal.TOTPPrincipal;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A {@link CredentialValidator} that checks for a {@link EsupOtpContext}.
 */
public class EsupOtpCredentialValidator extends AbstractCredentialValidator {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpCredentialValidator.class);

    /** Lookup strategy for TOTP context. */
    @Nonnull private Function<AuthenticationContext,EsupOtpContext> esupOtpContextLookupStrategy;
            
    /** A regular expression to apply for acceptance testing. */
    @Nullable private Pattern matchExpression;
    
    /** Constructor. */
    public EsupOtpCredentialValidator() {
        esupOtpContextLookupStrategy = new ChildContextLookup<>(EsupOtpContext.class);
    }
        
    /**
     * Set the lookup strategy to locate the {@link EsupOtpContext}.
     * 
     * @param strategy lookup strategy
     */
    public void setEsupOtpContextLookupStrategy(
            @Nonnull final Function<AuthenticationContext,EsupOtpContext> strategy) {
        checkSetterPreconditions();
        
        esupOtpContextLookupStrategy = Constraint.isNotNull(strategy, "EsupOtpContext lookup strategy cannot be null");
    }
    
    /**
     * Set a matching expression to apply to the username for acceptance. 
     * 
     * @param expression a matching expression
     */
    public void setMatchExpression(@Nullable final Pattern expression) {
        checkSetterPreconditions();
        
        matchExpression = expression;
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
    }

// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override
    protected Subject doValidate(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext,
            @Nullable final WarningHandler warningHandler,
            @Nullable final ErrorHandler errorHandler) throws Exception {
        
        final EsupOtpContext esupOtpContext = esupOtpContextLookupStrategy.apply(authenticationContext);
        if (esupOtpContext == null) {
            log.info("{} No EsupOtpContext available", getLogPrefix());
            if (errorHandler != null) {
                errorHandler.handleError(profileRequestContext, authenticationContext, AuthnEventIds.NO_CREDENTIALS,
                        AuthnEventIds.NO_CREDENTIALS);
            }
            throw new LoginException(AuthnEventIds.NO_CREDENTIALS);
        } else if (esupOtpContext.getUsername() == null) {
            log.info("{} No username available within EsupOtpContext", getLogPrefix());
            if (errorHandler != null) {
                errorHandler.handleError(profileRequestContext, authenticationContext, AuthnEventIds.UNKNOWN_USERNAME,
                        AuthnEventIds.UNKNOWN_USERNAME);
            }
            throw new LoginException(AuthnEventIds.NO_CREDENTIALS);
        } else if (esupOtpContext.getTokenCode() == null) {
            log.info("{} No tokencode available within EsupOtpContext", getLogPrefix());
            if (errorHandler != null) {
                errorHandler.handleError(profileRequestContext, authenticationContext, AuthnEventIds.NO_CREDENTIALS,
                        AuthnEventIds.NO_CREDENTIALS);
            }
            throw new LoginException(AuthnEventIds.NO_CREDENTIALS);
        }
        
//        if (EsupOtpContext.getTokenSeeds().isEmpty()) {
//            // Resolve seeds.
//            seedSource.accept(profileRequestContext);
//            
//            if (EsupOtpContext.getTokenSeeds().isEmpty()) {
//                log.info("{} No seeds were obtained for user '{}'", getLogPrefix(), EsupOtpContext.getUsername());
//                if (errorHandler != null) {
//                    errorHandler.handleError(profileRequestContext, authenticationContext,
//                            AuthnEventIds.INVALID_CREDENTIALS, AuthnEventIds.INVALID_CREDENTIALS);
//                }
//                throw new LoginException(AuthnEventIds.INVALID_CREDENTIALS);
//            }
//        }
        
        if (matchExpression != null && !matchExpression.matcher(esupOtpContext.getUsername()).matches()) {
            log.debug("{} Username '{}' did not match expression", getLogPrefix(), esupOtpContext.getUsername());
            return null;
        }
                
        log.debug("{} Attempting to authenticate token code for '{}' ", getLogPrefix(), esupOtpContext.getUsername());
        
        try {
            final Integer tokenCode = esupOtpContext.getTokenCode();
            // Checked above.
            assert tokenCode != null;
            
            if(esupOtpContext.getClient().postVerify(esupOtpContext.getUsername(), tokenCode.toString())) {
                log.info("{} Login by '{}' succeeded", getLogPrefix(), esupOtpContext.getUsername());
                return populateSubject(new Subject(), profileRequestContext, esupOtpContext);
            }
            
            throw new LoginException(AuthnEventIds.INVALID_CREDENTIALS);
        } catch (final Exception e) {
            log.info("{} Login by '{}' failed", getLogPrefix(), esupOtpContext.getUsername(), e);
            if (errorHandler != null) { 
                errorHandler.handleError(profileRequestContext, authenticationContext, e,
                        AuthnEventIds.INVALID_CREDENTIALS);
            }
            throw e;
        }
    }
// Checkstyle: CyclomaticComplexity ON
    

    /**
     * Decorate the subject with "standard" content from the validation.
     * 
     * @param subject the subject being returned
     * @param profileRequestContext current profile request context
     * @param esupOtpContext the TOTP context being validated
     * 
     * @return the decorated subject
     */
    @Nonnull protected Subject populateSubject(@Nonnull final Subject subject,
            @Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final EsupOtpContext esupOtpContext) {
        
        final String username = esupOtpContext.getUsername();
        // Checked earlier.
        assert username != null;
        subject.getPrincipals().add(new TOTPPrincipal(username));
        
        // Bypass c14n. We already operate on a canonical name, so just re-confirm it.
        profileRequestContext.ensureSubcontext(SubjectCanonicalizationContext.class).setPrincipalName(
                esupOtpContext.getUsername());
        
        return super.populateSubject(subject);
    }
    
}