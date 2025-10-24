package fr.renater.shibboleth.idp.plugin.authn.esup.otp.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;

import com.google.common.base.Strings;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Context class for state of a Esup otp validation.
 */
public class EsupOtpContext extends BaseContext {

    /** The subject identifier with respect to the token "back-end". */
    @Nullable @NotEmpty private String username;

    /** The token code supplied. */
    @Nullable private Integer tokenCode;


    /**
     * Get the username.
     *
     * @return the username
     */
    @Nullable @NotEmpty public String getUsername() {
        return username;
    }

    /**
     * Set the username.
     *
     * @param name the username
     *
     * @return this context
     */
    @Nonnull public EsupOtpContext setUsername(@Nullable @NotEmpty final String name) {
        if (Strings.isNullOrEmpty(name)) {
            username = null;
        } else {
            username = name;
        }

        return this;
    }

    /**
     * Get the token code.
     *
     * @return the token code
     */
    @Nullable public Integer getTokenCode() {
        return tokenCode;
    }

    /**
     * Set the token code.
     *
     * @param code the token code
     *
     * @return this context
     */
    @Nonnull public EsupOtpContext setTokenCode(@Nullable final Integer code) {
        tokenCode = code;

        return this;
    }

}
