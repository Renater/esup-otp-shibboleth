package fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.navigate;

import net.shibboleth.idp.authn.context.SubjectContext;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.function.Function;

/**
 * Pull out a username/principalName from the {@link SubjectContext#getPrincipalName()} if it exists.
 */
@ThreadSafe
public class UsernameLookupFromSubjectContext implements Function<ProfileRequestContext, String> {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(UsernameLookupFromSubjectContext.class);

    /** {@inheritDoc} */
    @Override
    public String apply(@Nullable final ProfileRequestContext input) {
        if (input == null) {
            log.trace("Profile context was null, can not find existing username");
            return null;
        }
        final SubjectContext subjectContext = input.getSubcontext(SubjectContext.class);
        if (subjectContext == null) {
            log.trace("Subject context was null, can not find existing username");
            return null;
        }    
        final String username = subjectContext.getPrincipalName();
        log.trace("Found existing username '{}' from subject context", username);
        return username;
    }

}
