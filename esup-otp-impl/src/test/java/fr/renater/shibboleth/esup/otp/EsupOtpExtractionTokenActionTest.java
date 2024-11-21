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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.webflow.execution.Event;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.EsupOtpContext;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl.EsupOtpExtractionTokenAction;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.profile.testing.ActionTestingSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.testing.ConstantSupplier;

import static fr.renater.shibboleth.idp.plugin.authn.esup.otp.util.EsupOtpUtils.PUSH_METHOD;
import static fr.renater.shibboleth.idp.plugin.authn.esup.otp.util.EsupOtpUtils.WEBAUTHN_METHOD;

/**
 *
 */
public class EsupOtpExtractionTokenActionTest extends BaseAuthenticationContextTest {
    
    private EsupOtpExtractionTokenAction action;

    private EsupOtpContext esupOtpContext;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        super.setUp();
        
        action = new EsupOtpExtractionTokenAction();
        addEsupOtpContext();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        action.setHttpServletRequestSupplier(new ConstantSupplier<>(request));
        action.setUsernameLookupStrategy(FunctionSupport.constant("jdoe"));

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.setUsersSecret("anUsersSecret");
        defaultEsupOtpIntegration.initialize();

        action.setEsupOtpIntegrationLookupStrategy(prc -> defaultEsupOtpIntegration);
        action.initialize();
    }

    @Test public void testNoServlet() throws Exception {
        action = new EsupOtpExtractionTokenAction();
        addEsupOtpContext();
        action.setUsernameLookupStrategy(FunctionSupport.constant("jdoe"));

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.initialize();

        action.setEsupOtpIntegrationLookupStrategy(prc -> defaultEsupOtpIntegration);

        action.initialize();
        final Event event = action.execute(src);

        ActionTestingSupport.assertEvent(event, AuthnEventIds.NO_CREDENTIALS);
    }

    @Test public void testNoUsername() throws Exception {
        action = new EsupOtpExtractionTokenAction();
        addEsupOtpContext();
        eoc.setUsername(null);
        action.setUsernameLookupStrategy(FunctionSupport.constant(null));

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.initialize();

        action.setEsupOtpIntegrationLookupStrategy(prc -> defaultEsupOtpIntegration);

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

    @Test public void testInvalidFormat() throws Exception {
        if (action.getHttpServletRequest() instanceof MockHttpServletRequest mock) {
            mock.addParameter("tokencode", "A123456");
        }

        final Event event = action.execute(src);
        ActionTestingSupport.assertEvent(event, AuthnEventIds.INVALID_CREDENTIALS);
    }

    @Test public void testValid() throws Exception {
        eoc.setTransportChoose(PUSH_METHOD);
        if (action.getHttpServletRequest() instanceof MockHttpServletRequest mock) {
            mock.addParameter("tokencode", "123456");
        }

        final Event event = action.execute(src);
        ActionTestingSupport.assertProceedEvent(event);
        final AuthenticationContext authCtx = prc.ensureSubcontext(AuthenticationContext.class);
        final EsupOtpContext esupOtpCtx = authCtx.getSubcontext(EsupOtpContext.class);
        assert esupOtpCtx != null;
        Assert.assertEquals(esupOtpCtx.getUsername(), "jdoe");
        Assert.assertEquals(esupOtpCtx.getTokenCode(), Integer.valueOf(123456));
    }

    @Test public void testValidWebauthn() throws Exception {
        eoc.setTransportChoose(WEBAUTHN_METHOD);
        if (action.getHttpServletRequest() instanceof MockHttpServletRequest mock) {
            mock.addParameter("publicKeyCredential", "{\"id\":\"9_EzXuJQy79lJArC9J-oXoD_1biPFMBMhnbyegSRrsg\",\"response\":{\"authenticatorData\":\"S7wmxUIOh13coAzoaY4w9wC3y0Bm_MgckM-PKf_tIikFAAAAAQ\",\"signature\":\"MEQCIEGhq_MogJ9PvY6Vg_WcHgKlQ8YLsJPGhJ-m62D-7yCgAiARzjsMeXkjI5zssglPTh5U2JH28JGihtHNJrBfEktkqA\",\"userHandle\":\"dGhpc2lzYWNoYWxsZW5nZQ\",\"clientDataJSON\":\"eyJvcmlnaW4iOiJodHRwczovL2lkcC5leGFtcGxlLmNvbSIsImNoYWxsZW5nZSI6ImRHaHBjMmx6QmFOb1lXeHNaVzVuWlE9PSIsInR5cGUiOiJ3ZWJhdXRobi5nZXQifQ\"},\"clientExtensionResults\":{\"extensionIds\":[]},\"type\":\"public-key\",\"rawId\":\"9_EzXuJQy79lJArC9J-oXoD_1biPFMBMhnbyegSRrsg\"}");
        }

        final Event event = action.execute(src);
        ActionTestingSupport.assertProceedEvent(event);
        final AuthenticationContext authCtx = prc.ensureSubcontext(AuthenticationContext.class);
        final EsupOtpContext esupOtpCtx = authCtx.getSubcontext(EsupOtpContext.class);
        assert esupOtpCtx != null;
        Assert.assertEquals(esupOtpCtx.getUsername(), "jdoe");
        Assert.assertNotNull(esupOtpCtx.getPublicKeyCredentialAssertionResponse());
        //Assert.assertEquals(esupOtpCtx.getPublicKeyCredentialAssertionResponse(), Integer.valueOf(123456));
    }

}
