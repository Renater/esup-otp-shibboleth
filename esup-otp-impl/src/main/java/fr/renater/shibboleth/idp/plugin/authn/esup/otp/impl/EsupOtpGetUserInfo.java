package fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl;


import java.util.function.Function;
import javax.annotation.Nonnull;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.EsupOtpContext;
import net.shibboleth.idp.authn.AbstractAuthenticationAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.AuthenticationErrorContext;
import net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An action that get esup otp user informations from a username from a lookup strategy,
 * creates a {@link EsupOtpContext}, and attaches it to the {@link AuthenticationContext}.
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link AuthnEventIds#NO_CREDENTIALS}
 * @pre <pre>ProfileRequestContext.getSubcontext(AuthenticationContext.class) != null</pre>
 * @post <pre>AuthenticationContext.getSubcontext(EsupOtpContext.class) != null</pre>
 */
public class EsupOtpGetUserInfo extends AbstractAuthenticationAction {

	/** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpGetUserInfo.class);
    
    /** Lookup strategy for username to use in resolving token seeds. */
    @Nonnull private Function<ProfileRequestContext, String> usernameLookupStrategy;
    
    /** Creation strategy for esup otp context. */
    @Nonnull private Function<AuthenticationContext, EsupOtpContext> esupOtpContextCreationStrategy;

	/**
	 * Constructor.
	 *
	 */
	public EsupOtpGetUserInfo() {
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

    /** {@inheritDoc} */
    @Override protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull ProfileRequestContext profileRequestContext,
    		@Nonnull AuthenticationContext authenticationContext) {
    	
    	// Clear error state.
        authenticationContext.removeSubcontext(AuthenticationErrorContext.class);
        
        final EsupOtpContext esupOtpContext = esupOtpContextCreationStrategy.apply(authenticationContext);
        if (esupOtpContext == null) {
            log.warn("{} Unable to create esup otp context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return;
        }

        esupOtpContext.setTokenCode(null);
        
        // Fill in username if not set.
        if (esupOtpContext.getUsername() == null) {
            final String username = usernameLookupStrategy.apply(profileRequestContext);
            if (username == null) {
                log.warn("{} No principal name available", getLogPrefix());
                ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
                return;
            }
            esupOtpContext.setUsername(username);
        }
    }

}
