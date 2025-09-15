package fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl;


import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import fr.renater.shibboleth.esup.otp.EsupOtpPrincipal;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.EsupOtpContext;
import net.shibboleth.idp.authn.AbstractCredentialValidator;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.CredentialValidator;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A {@link CredentialValidator} that checks {@link EsupOtpContext}.
 */
public class EsupOtpCredentialValidator extends AbstractCredentialValidator {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpCredentialValidator.class);

    /** Lookup strategy for EsupOtp context. */
    @Nonnull private Function<AuthenticationContext, EsupOtpContext> esupOtpContextLookup;

    /** The registry for locating EsupOtpClient */
    @NonnullAfterInit
    private EsupOtpClientRegistry clientRegistry;
    
    /** Constructor. */
    public EsupOtpCredentialValidator() {
        esupOtpContextLookup = new ChildContextLookup<>(EsupOtpContext.class);
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
        
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (clientRegistry ==  null) {
            throw new ComponentInitializationException("EsupOtp Client Registry cannot be null");
        }
    }

// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override
    protected Subject doValidate(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext,
            @Nullable final WarningHandler warningHandler,
            @Nullable final ErrorHandler errorHandler) throws Exception {
        
        final EsupOtpContext esupOtpContext = esupOtpContextLookup.apply(authenticationContext);
        if (esupOtpContext == null) {
            log.info("{} No EsupOtpContext available", getLogPrefix());
            if (errorHandler != null) {
                errorHandler.handleError(profileRequestContext, authenticationContext, AuthnEventIds.NO_CREDENTIALS,
                        AuthnEventIds.NO_CREDENTIALS);
            }
            throw new LoginException(AuthnEventIds.NO_CREDENTIALS);
        }

        final String username = esupOtpContext.getUsername();
        if(username == null) {
            log.info("{} No username available within EsupOtpContext", getLogPrefix());
            if (errorHandler != null) {
                errorHandler.handleError(profileRequestContext, authenticationContext, AuthnEventIds.NO_CREDENTIALS,
                        AuthnEventIds.NO_CREDENTIALS);
            }
            throw new LoginException(AuthnEventIds.NO_CREDENTIALS);
        }

        final Integer tokenCode = esupOtpContext.getTokenCode();
            if(tokenCode == null) {
                log.warn("{} No tokencode available within EsupOtpContext", getLogPrefix());
                if (errorHandler != null) {
                    errorHandler.handleError(profileRequestContext, authenticationContext, AuthnEventIds.NO_CREDENTIALS,
                            AuthnEventIds.NO_CREDENTIALS);
                }
                throw new LoginException(AuthnEventIds.NO_CREDENTIALS);
            }

        log.debug("{} Attempting to authenticate token code for '{}' ", getLogPrefix(), esupOtpContext.getUsername());
        
        final EsupOtpClient client = clientRegistry.getClient();       
        try {
                if(client.postVerify(username, tokenCode.toString())) {
                    log.info("{} Login by '{}' succeeded", getLogPrefix(), esupOtpContext.getUsername());
                    var subject = new Subject();
                    subject.getPrincipals().add(new EsupOtpPrincipal(username));
                    return subject;
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

}
