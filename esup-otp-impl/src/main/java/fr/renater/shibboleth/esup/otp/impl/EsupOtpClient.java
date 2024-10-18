/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.renater.shibboleth.esup.otp.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;

import fr.renater.shibboleth.esup.otp.EsupOtpIntegration;
import fr.renater.shibboleth.esup.otp.config.EsupOtpRestTemplate;
import fr.renater.shibboleth.esup.otp.connector.EsupOtpClientInitializationException;
import fr.renater.shibboleth.esup.otp.connector.EsupOtpConnector;
import fr.renater.shibboleth.esup.otp.connector.impl.EsupOtpConnectorImpl;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 *
 */
@ThreadSafe
public class EsupOtpClient extends AbstractIdentifiableInitializableComponent {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpClient.class);
    
    /** Registry of Duo client to Duo integration.*/
    @Nonnull @NonnullElements private final ConcurrentMap<EsupOtpIntegration, EsupOtpConnector> clientRegistry;
    
    /** Function for creating a DuoClient from a DuoIntegration. */
    @Nonnull private final Function<EsupOtpIntegration, EsupOtpConnector> clientRegistryMappingFunction;

    /**
     * Constructor.
     *
     */
    public EsupOtpClient() {
        clientRegistry = new ConcurrentHashMap<>(1);
        clientRegistryMappingFunction = new CreateNewClientMappingFunction();
    }
    
    /**
     * Get or create esup otp connector.
     * 
     * @param integration
     * @return esup otp connector
     */
    @Nonnull public EsupOtpConnector getClientOrCreate(@Nonnull final EsupOtpIntegration integration) {
        Constraint.isNotNull(integration, "Duo integration can not be null");
        
        final EsupOtpConnector client = clientRegistry.computeIfAbsent(integration, clientRegistryMappingFunction);
        log.trace("Client registry returning the EsupOtpConnector instance of type '{}'", 
                client.getClass().getCanonicalName());
        return client;
    }
    
    /**
     * A function for creating a new Esup otp client from the configured client factory for the given Duo integration.
     * throws a {@link EsupOtpClientInitializationException} if the factory can not create the client.
     */
    @ThreadSafe
    private final class CreateNewClientMappingFunction implements Function<EsupOtpIntegration, EsupOtpConnector> {
        
        /** Class logger. */
        @Nonnull private final Logger log = LoggerFactory.getLogger(CreateNewClientMappingFunction.class);
        
        @Override
        @Nonnull public EsupOtpConnector apply(@Nullable final EsupOtpIntegration integration){
            
//            try {
                assert integration != null;
                log.debug("Creating a new Esup otp client for integration '{}'",integration);
                return new EsupOtpConnectorImpl(new EsupOtpRestTemplate(integration));
//            } catch (final DuoClientException e) {
//                //wrap the exception in a runtime exception.
//                throw new EsupOtpClientInitializationException("Could not initialise "
//                        + "the EsupOtpClient for the integration with clientId "+integration.getClientId(),e);
//            }           
        }
        
    }

}
