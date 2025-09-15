package fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl;


import fr.renater.shibboleth.esup.otp.DefaultEsupOtpIntegration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.webflow.execution.Event;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.EsupOtpContext;
import net.shibboleth.idp.authn.AuthnEventIds;
import net.shibboleth.idp.authn.context.AuthenticationContext;
import net.shibboleth.idp.profile.testing.ActionTestingSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.testing.ConstantSupplier;

/**
 *
 */
public class EsupOtpExtractionTokenActionTest extends BaseAuthenticationContextTest {
    
    private EsupOtpExtractionTokenAction action;

    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        super.setUp();
        
        action = new EsupOtpExtractionTokenAction();
        addEsupOtpContext();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        action.setHttpServletRequestSupplier(new ConstantSupplier<>(request));

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.setUsersSecret("anUsersSecret");
        defaultEsupOtpIntegration.initialize();

        action.initialize();
    }

    @Test public void testNoServlet() throws Exception {
        action = new EsupOtpExtractionTokenAction();
        addEsupOtpContext();

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.initialize();

        action.initialize();
        final Event event = action.execute(src);

        ActionTestingSupport.assertEvent(event, AuthnEventIds.NO_CREDENTIALS);
    }

    @Test public void testNoUsername() throws Exception {
        action = new EsupOtpExtractionTokenAction();
        addEsupOtpContext();
        eoc.setUsername(null);

        final DefaultEsupOtpIntegration defaultEsupOtpIntegration = new DefaultEsupOtpIntegration();
        defaultEsupOtpIntegration.setAPIHost("https://tobedefine.fr");
        defaultEsupOtpIntegration.initialize();

        action.initialize();
        final Event event = action.execute(src);

        ActionTestingSupport.assertEvent(event, AuthnEventIds.NO_CREDENTIALS);
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

}
