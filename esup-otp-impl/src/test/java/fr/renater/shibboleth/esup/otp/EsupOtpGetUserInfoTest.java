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

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Map;

import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.webflow.execution.Event;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClientException;
import fr.renater.shibboleth.esup.otp.dto.user.EsupOtpUserInfoResponse;
import fr.renater.shibboleth.esup.otp.dto.user.UserMethods;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.EsupOtpContext;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl.EsupOtpClientRegistry;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl.EsupOtpGetUserInfo;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.profile.testing.ActionTestingSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.testing.ConstantSupplier;

/**
 *
 */
public class EsupOtpGetUserInfoTest extends BaseAuthenticationContextTest {
    
    private EsupOtpGetUserInfo action;

    private EsupOtpContext esupOtpContext;

    private EsupOtpClient mockClient;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        super.setUp();
        
        action = new EsupOtpGetUserInfo();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        action.setHttpServletRequestSupplier(new ConstantSupplier<>(request));
        action.setUsernameLookupStrategy(FunctionSupport.constant("jdoe"));

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.setUsersSecret("anUsersSecret");
        defaultEsupOtpIntegration.setSupportedMethods(List.of("random_code", "push"));
        defaultEsupOtpIntegration.initialize();

        action.setEsupOtpIntegrationLookupStrategy(prc -> defaultEsupOtpIntegration);

        final EsupOtpClientRegistry mockClientRegistry = Mockito.mock(EsupOtpClientRegistry.class);
        mockClient = Mockito.mock(EsupOtpClient.class);
        Mockito.when(mockClientRegistry.getClientOrCreate(any())).thenReturn(mockClient);

        action.setClientRegistry(mockClientRegistry);

        action.initialize();

        esupOtpContext = prc.ensureSubcontext(AuthenticationContext.class).ensureSubcontext(EsupOtpContext.class);
    }
    
    @Test public void testNoUsername() throws Exception {
        action = new EsupOtpGetUserInfo();
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

    @Test public void testClientException() throws Exception {

        EsupOtpUserInfoResponse esupOtpResponse = new EsupOtpUserInfoResponse();
        esupOtpResponse.setCode("Nok");

        Mockito.when(mockClient.getUserInfos(any())).thenThrow(EsupOtpClientException.class);

        final Event event = action.execute(src);

        ActionTestingSupport.assertEvent(event, "ClientException");

    }

    @Test public void testNokEsupOtpResponse() throws Exception {

        EsupOtpUserInfoResponse esupOtpResponse = new EsupOtpUserInfoResponse();
        esupOtpResponse.setCode("Nok");

        Mockito.when(mockClient.getUserInfos(any())).thenReturn(esupOtpResponse);

        final Event event = action.execute(src);

        ActionTestingSupport.assertEvent(event, AuthnEventIds.UNKNOWN_USERNAME);

    }

    @Test public void testValid() throws Exception {

        EsupOtpUserInfoResponse esupOtpResponse = new EsupOtpUserInfoResponse();
        esupOtpResponse.setCode("Ok");
        EsupOtpUserInfoResponse.User user = new EsupOtpUserInfoResponse.User();
        UserMethods methods = new UserMethods();
        UserMethods.UserMethod randomCodeMethod = new UserMethods.UserMethod();
        randomCodeMethod.setActive(true);
        randomCodeMethod.setTransports(List.of("sms"));
        methods.setRandomCode(randomCodeMethod);
        user.setMethods(methods);
        EsupOtpUserInfoResponse.User.Transports transports = new EsupOtpUserInfoResponse.User.Transports();
        transports.setSms("06******398");
        transports.setMail("ant*******@*******er.fr");
        transports.setPush("Model Telephone");
        user.setTransports(transports);
        esupOtpResponse.setUser(user);

        Mockito.when(mockClient.getUserInfos(any())).thenReturn(esupOtpResponse);

        final Event event = action.execute(src);
        ActionTestingSupport.assertProceedEvent(event);

        Mockito.verify(mockClient).getUserInfos("jdoe");
        Assert.assertEquals(esupOtpContext.getUsername(), "jdoe");
        Assert.assertNull(esupOtpContext.getTokenCode());
        Assert.assertEquals(esupOtpContext.getEnabledChoices(), List.of("random_code.sms"));
        Assert.assertEquals(esupOtpContext.getConfiguredTransports(), Map.of("sms","06******398", "mail", "ant*******@*******er.fr", "push", "Model Telephone"));
    }

}
