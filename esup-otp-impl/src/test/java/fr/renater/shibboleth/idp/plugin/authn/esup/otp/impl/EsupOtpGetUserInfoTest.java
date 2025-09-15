package fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl;

import fr.renater.shibboleth.esup.otp.DefaultEsupOtpIntegration;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.webflow.execution.Event;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.EsupOtpContext;
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
        defaultEsupOtpIntegration.initialize();

        final EsupOtpClientRegistry mockClientRegistry = Mockito.mock(EsupOtpClientRegistry.class);
        mockClientRegistry.setIntegration(defaultEsupOtpIntegration);
        mockClient = Mockito.mock(EsupOtpClient.class);
        Mockito.when(mockClientRegistry.getClient()).thenReturn(mockClient);

        action.initialize();

        esupOtpContext = prc.ensureSubcontext(AuthenticationContext.class).ensureSubcontext(EsupOtpContext.class);
    }

    @Test public void testNoUsername() throws Exception {
        action = new EsupOtpGetUserInfo();
        action.setUsernameLookupStrategy(FunctionSupport.constant(null));

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.initialize();

        action.initialize();

        final Event event = action.execute(src);

        ActionTestingSupport.assertEvent(event, AuthnEventIds.UNKNOWN_USERNAME);
    }

    @Test public void testValid() throws Exception {
        final Event event = action.execute(src);
        ActionTestingSupport.assertProceedEvent(event);

        Assert.assertEquals(esupOtpContext.getUsername(), "jdoe");
        Assert.assertNull(esupOtpContext.getTokenCode());
    }

}
