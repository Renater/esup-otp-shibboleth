package fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.navigate;

import net.shibboleth.idp.session.context.navigate.CanonicalUsernameLookupStrategy;
import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * {@inheritDoc}
 *
 * If withScope is set to true and username not contains
 */
@ThreadSafe
public class CanonicalUsernameWithScopeLookupStrategy extends CanonicalUsernameLookupStrategy {

    /** Class logger. */
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(CanonicalUsernameWithScopeLookupStrategy.class);

    private String delimiter;

    private boolean withScope;

    private String scope;

    public CanonicalUsernameWithScopeLookupStrategy() {
        delimiter = "@";
    }

    /**
     * Set the delimiter to use for serializing scoped attribute values.
     *
     * @param ch delimiter to use
     */
    public void setScopedDelimiter(final String ch) {
        if(ch.length() != 1 ) {
            throw new ConstraintViolationException("Invalid scoped delimiter length");
        }
        delimiter = ch;
    }

    /**
     * Set if scope is used
     *
     * @param with withScope
     */
    public void setWithScope(final boolean with) {
        withScope = with;
        log.debug("CanonicalUsernameWithScopeLookupStrategy want to canonicalize username {} scope", withScope ? "with" : "without");
    }

    /**
     * Set if idp.scope is used
     *
     * @param s withScope
     */
    public void setScope(final String s) {
        scope = s;
        log.debug("Scope used for CanonicalUsernameWithScope : {}", scope);
    }

    /** {@inheritDoc} */
    @Override
    public String apply(@Nullable final ProfileRequestContext input) {
        String username = super.apply(input);

        if (username != null && withScope && !username.contains(delimiter)) {
            log.debug("Add scope {} with delimiter {} to username {}", scope, delimiter, username);
            username = username + delimiter + scope;
        }
        return username;
    }

}