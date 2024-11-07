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

package fr.renater.shibboleth.esup.otp;

import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.EsupOtpContext;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl.EsupOtpClientRegistry;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.webflow.execution.Event;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl.EsupOtpExtractionAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.profile.testing.ActionTestingSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.testing.ConstantSupplier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

/**
 *
 */
public class EsupOtpExtractionActionTest extends BaseAuthenticationContextTest {
    
    private EsupOtpExtractionAction action;

    private EsupOtpContext esupOtpContext;

    private EsupOtpClient mockClient;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        super.setUp();
        
        action = new EsupOtpExtractionAction();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        action.setHttpServletRequestSupplier(new ConstantSupplier<>(request));
        action.setUsernameLookupStrategy(FunctionSupport.constant("jdoe"));

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.setUsersSecret("anUsersSecret");
        defaultEsupOtpIntegration.initialize();

        action.setEsupOtpIntegrationLookupStrategy(prc -> defaultEsupOtpIntegration);

        final EsupOtpClientRegistry mockClientRegistry = Mockito.mock(EsupOtpClientRegistry.class);
        mockClient = Mockito.mock(EsupOtpClient.class);
        Mockito.when(mockClientRegistry.getClientOrCreate(any())).thenReturn(mockClient);

        action.setClientRegistry(mockClientRegistry);

        action.initialize();

        esupOtpContext = prc.ensureSubcontext(AuthenticationContext.class).ensureSubcontext(EsupOtpContext.class);
    }
    
    @Test public void testNoServlet() throws Exception {
        action = new EsupOtpExtractionAction();
        action.setUsernameLookupStrategy(FunctionSupport.constant("jdoe"));

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.initialize();

        action.setEsupOtpIntegrationLookupStrategy(prc -> defaultEsupOtpIntegration);

        final EsupOtpClientRegistry mockClientRegistry = Mockito.mock(EsupOtpClientRegistry.class);
        action.setClientRegistry(mockClientRegistry);

        action.initialize();
        final Event event = action.execute(src);
        
        ActionTestingSupport.assertEvent(event, AuthnEventIds.NO_CREDENTIALS);
    }
    
    @Test public void testNoUsername() throws Exception {
        action = new EsupOtpExtractionAction();
        action.setUsernameLookupStrategy(FunctionSupport.constant(null));

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.initialize();

        action.setEsupOtpIntegrationLookupStrategy(prc -> defaultEsupOtpIntegration);

        final EsupOtpClientRegistry mockClientRegistry = Mockito.mock(EsupOtpClientRegistry.class);
        action.setClientRegistry(mockClientRegistry);

        action.initialize();

        final Event event = action.execute(src);
        
        ActionTestingSupport.assertEvent(event, AuthnEventIds.UNKNOWN_USERNAME);
    }

    @Test public void testMissingField() throws Exception {
        final Event event = action.execute(src);
        ActionTestingSupport.assertEvent(event, AuthnEventIds.NO_CREDENTIALS);
    }

    @Test public void testWrongField() throws Exception {
        if (action.getHttpServletRequest() instanceof MockHttpServletRequest mock) {
            mock.addParameter("Bar", "123456");
        }

        final Event event = action.execute(src);
        ActionTestingSupport.assertEvent(event, AuthnEventIds.NO_CREDENTIALS);
    }

    /*@Test public void testInvalidFormat() throws Exception {
        if (action.getHttpServletRequest() instanceof MockHttpServletRequest mock) {
            mock.addParameter("tokencode", "A123456");
        }

        final Event event = action.execute(src);
        ActionTestingSupport.assertEvent(event, AuthnEventIds.INVALID_CREDENTIALS);
    }*/

    @Test public void testValidRandomCodeSms() throws Exception {
        if (action.getHttpServletRequest() instanceof MockHttpServletRequest mock) {
            mock.addParameter("transportchoose", "random_code.sms");
        }

        Map<String, String> transportConfigured = new HashMap<>();
        transportConfigured.put("random_code.sms", "06******398");
        esupOtpContext.setConfiguredTransports(transportConfigured);

        EsupOtpResponse esupOtpResponse = new EsupOtpResponse();
        esupOtpResponse.setCode("Ok");
        Mockito.when(mockClient.postSendMessage(any(), any(), any())).thenReturn(esupOtpResponse);

        final Event event = action.execute(src);
        ActionTestingSupport.assertProceedEvent(event);

        Assert.assertEquals(esupOtpContext.getUsername(), "jdoe");
        Assert.assertNull(esupOtpContext.getTokenCode());

        Mockito.verify(mockClient).postSendMessage("jdoe", "random_code", "sms");
    }

    @Test public void testValidPush() throws Exception {
        if (action.getHttpServletRequest() instanceof MockHttpServletRequest mock) {
            mock.addParameter("transportchoose", "push");
        }

        Map<String, String> transportConfigured = new HashMap<>();
        transportConfigured.put("push", "Model Telephone");
        esupOtpContext.setConfiguredTransports(transportConfigured);

        EsupOtpResponse esupOtpResponse = new EsupOtpResponse();
        esupOtpResponse.setCode("Ok");
        Mockito.when(mockClient.postSendMessage(any(), any(), any())).thenReturn(esupOtpResponse);

        final Event event = action.execute(src);
        ActionTestingSupport.assertProceedEvent(event);

        Assert.assertEquals(esupOtpContext.getUsername(), "jdoe");
        Assert.assertNull(esupOtpContext.getTokenCode());

        Mockito.verify(mockClient).postSendMessage("jdoe", "push", "push");
    }

}
