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

import net.shibboleth.idp.attribute.IdPAttribute;
import net.shibboleth.idp.attribute.IdPAttributeValue;
import net.shibboleth.idp.attribute.ScopedStringAttributeValue;
import net.shibboleth.idp.attribute.StringAttributeValue;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

/**
 * An {@link AbstractAttributeContextUserIdentityStrategy} that pulls out an {@link StringAttributeValue} from the 
 * attribute context. Will return {@code null} if the attribute can not be found, or if there is more than one 
 * attribute value.
 */
@ThreadSafe
public class AttributeContextStringLookupStrategy extends AbstractAttributeContextUserIdentityStrategy<String> {
        
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AttributeContextStringLookupStrategy.class);
        
    /** {@inheritDoc} */
    @Override
    @Nullable public String apply(final ProfileRequestContext profileRequestContext) {
        if (profileRequestContext == null) {
            return null;
        }

        final IdPAttribute attribute = getAttribute(profileRequestContext);
        if (attribute == null) {
            return null;
        }

        final List<IdPAttributeValue> values = attribute.getValues();
        if (values.size() != 1) {
            log.warn("{}: Attribute '{}' has more than one value", getId(), getAttributeId());
            return null;
        }
        final IdPAttributeValue value = values.get(0);
        if (value instanceof final ScopedStringAttributeValue scopedStrAttributeValue) {
            logDebugFoundAttribute(attribute.getId(), scopedStrAttributeValue.getDisplayValue());
            return scopedStrAttributeValue.getDisplayValue();
        } else if (value instanceof final StringAttributeValue strValue) {
            logDebugFoundAttribute(attribute.getId(), strValue.getValue());
            return strValue.getValue();
        }

        log.warn("{}: Attribute '{}' could not be found", getId(), getAttributeId());
        return null;
    }

    private void logDebugFoundAttribute(String attributeId, String attributeValue) {
        if(log.isDebugEnabled()) {
            log.debug("{}: Found attribute '{}' with value '{}'", getId(), attributeId, attributeValue);
        }
    }

}
