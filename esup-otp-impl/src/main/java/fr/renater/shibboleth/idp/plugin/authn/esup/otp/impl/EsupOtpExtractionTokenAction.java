package fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.idp.authn.AbstractAuthenticationAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.authn.context.AuthenticationErrorContext;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.EsupOtpContext;
import jakarta.servlet.http.HttpServletRequest;


/**
 * An action that gets otp code from form or header, adds it to {@link EsupOtpContext}.
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link AuthnEventIds#NO_CREDENTIALS}
 * @event {@link AuthnEventIds#INVALID_CREDENTIALS}
 * @pre <pre>ProfileRequestContext.getSubcontext(AuthenticationContext.class) != null</pre>
 * @post <pre>AuthenticationContext.getSubcontext(EsupOtpContext.class) != null</pre>
 */
public class EsupOtpExtractionTokenAction extends AbstractAuthenticationAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpExtractionTokenAction.class);
    
    /** esup otp context getter */
    @Nonnull private Function<AuthenticationContext, EsupOtpContext> esupOtpContextLookup;

    /** Constructor. */
    public EsupOtpExtractionTokenAction() {
        esupOtpContextLookup = new ChildContextLookup<>(EsupOtpContext.class);
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final AuthenticationContext authenticationContext) {

        // Clear error state.
        authenticationContext.removeSubcontext(AuthenticationErrorContext.class);
        
        final EsupOtpContext esupOtpContext = esupOtpContextLookup.apply(authenticationContext);
        if (esupOtpContext == null) {
            log.warn("{} Unable to get esup otp context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return;
        }

        esupOtpContext.setTokenCode(null);
        
        final HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            log.debug("{} Profile action does not contain an HttpServletRequest", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
            return;
        }

            final String code =  extractCode(request);
            if (code == null) {
                ActionSupport.buildEvent(profileRequestContext, AuthnEventIds.NO_CREDENTIALS);
                return;
            }

            try {
                esupOtpContext.setTokenCode(Integer.valueOf(code));
                log.debug("Get token code : {}", esupOtpContext.getTokenCode());
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
