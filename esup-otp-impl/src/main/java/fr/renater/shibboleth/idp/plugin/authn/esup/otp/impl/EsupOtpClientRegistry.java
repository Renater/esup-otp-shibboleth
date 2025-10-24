package fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;

import fr.renater.shibboleth.esup.otp.DefaultEsupOtpIntegration;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClientInitializationException;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Esup otp client registry to get or create esup otp client.
 */
@ThreadSafe
public class EsupOtpClientRegistry extends AbstractIdentifiableInitializableComponent {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpClientRegistry.class);
    
    @NonnullAfterInit private EsupOtpClient client;

    /**
     * Constructor.
     *
     */
    public EsupOtpClientRegistry() {
    }
    
    /**
     * Get esup otp client
     * 
     * @return esup otp client
     */
    @Nonnull public EsupOtpClient getClient() {
        return client;
    }

    public synchronized void setIntegration(@Nonnull final DefaultEsupOtpIntegration integration) {
        client = new EsupOtpClientImpl(integration);
    }
    
    /**
     * A function for creating a new Esup otp client from the configured client factory for the given EsupOtp integration.
     * throws a {@link EsupOtpClientInitializationException} if the factory can not create the client.
     */
    @ThreadSafe
    private final class CreateNewClientMappingFunction implements Function<DefaultEsupOtpIntegration, EsupOtpClient> {
        
        /** Class logger. */
        @Nonnull private final Logger log = LoggerFactory.getLogger(CreateNewClientMappingFunction.class);
        
        @Override
        @Nonnull public EsupOtpClient apply(@Nullable final DefaultEsupOtpIntegration integration){
            assert integration != null;
            log.debug("Creating a new Esup otp client for integration '{}'",integration);
            return new EsupOtpClientImpl(integration);
        }
        
    }

}
