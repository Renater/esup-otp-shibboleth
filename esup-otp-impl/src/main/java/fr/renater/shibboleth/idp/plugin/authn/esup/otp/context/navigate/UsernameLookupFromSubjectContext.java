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
