package fr.renater.shibboleth.esup.otp;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import org.slf4j.Logger;

import net.shibboleth.idp.authn.principal.PrincipalSupportingComponent;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Wrapper for use of esup otp api.
 */
@ThreadSafe
public final class DefaultEsupOtpIntegration extends AbstractInitializableComponent implements PrincipalSupportingComponent {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DefaultEsupOtpIntegration.class);

    /** API host. */
    @GuardedBy("this") @NonnullAfterInit @NotEmpty private String apiHost;

    /** Users secret. */
    @GuardedBy("this") @NonnullAfterInit @NotEmpty private String usersSecret;

    /** API password. */
    @GuardedBy("this") @NonnullAfterInit @NotEmpty private String apiPassword;

    /** Issuer. */
    @GuardedBy("this") @NonnullAfterInit @NotEmpty private String issuer;

    /** The URL path to the health endpoint. */
    @GuardedBy("this") @Nullable private String healthEndpoint;

    /**
     * Constructor.
     *
     */
    public DefaultEsupOtpIntegration() {
    }

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public synchronized String getAPIHost() {
        checkComponentActive();
        assert apiHost != null;
        return apiHost;
    }

    /**
     * Set the API host to use.
     *
     * @param host API host
     */
    public synchronized void setAPIHost(@Nonnull @NotEmpty final String host) {
        checkSetterPreconditions();
        apiHost = Constraint.isNotNull(StringSupport.trimOrNull(host), "API host cannot be null or empty");
    }

    /**
     * Set the users secret to use.
     *
     * @param usrSecret secret key
     */
    public synchronized void setUsersSecret(@Nonnull @NotEmpty final String secret) {
        checkSetterPreconditions();
        usersSecret = StringSupport.trimOrNull(secret);
    }

    /** {@inheritDoc} */
    @NotEmpty public synchronized String getUsersSecret() {
        return usersSecret;
    }

    /**
     * Set the api password to use.
     *
     * @param apiPwd secret key
     */
    public synchronized void setApiPassword(@Nullable final String password) {
        checkSetterPreconditions();
        apiPassword = StringSupport.trimOrNull(password);
    }

    /** {@inheritDoc} */
    @Nullable public synchronized String getApiPassword() {
        return apiPassword;
    }

    /**
     * Set the issuer to use.
     *
     * @param iss generally equal to ${idp.entityID}
     */
    public synchronized void setIssuer(@Nullable final String iss) {
        checkSetterPreconditions();
        issuer = StringSupport.trimOrNull(iss);
    }

    /** {@inheritDoc} */
    @Nullable public synchronized String getIssuer() {
        return issuer;
    }

    /** {@inheritDoc} */
    @Nullable public synchronized String getHealthCheckEndpoint() {
        checkComponentActive();
        return healthEndpoint;
    }

    /**
     * Set the health check endpoint URL path.
     *
     * @param endpoint the endpoint.
     */
    public synchronized void setHealthCheckEndpoint(@Nullable final String endpoint) {
        checkSetterPreconditions();
        healthEndpoint = StringSupport.trimOrNull(endpoint);
    }

    /** {@inheritDoc} */
    @Override
    public @Nonnull @Unmodifiable @NotLive <T extends Principal> Set<T> getSupportedPrincipals(
            @Nonnull final Class<T> c) {
        return new HashSet<T>();
    }

}
