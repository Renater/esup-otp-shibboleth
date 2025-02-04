package fr.renater.shibboleth.idp.plugin.authn.esup.otp.context.navigate;

import net.shibboleth.idp.authn.context.SubjectContext;
import net.shibboleth.idp.profile.context.navigate.WebflowRequestContextProfileRequestContextLookup;
import net.shibboleth.idp.profile.testing.RequestContextBuilder;
import org.opensaml.profile.context.ProfileRequestContext;
import org.springframework.webflow.execution.RequestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

public class UsernameLookupFromSubjectContextTest {

    private UsernameLookupFromSubjectContext strategy;

    protected ProfileRequestContext prc;

    protected RequestContext src;

    @BeforeMethod
    public void setup() throws Exception {
        src = new RequestContextBuilder().buildRequestContext();
        prc = new WebflowRequestContextProfileRequestContextLookup().apply(src);
        strategy = new UsernameLookupFromSubjectContext();
    }

    @Test
    public void testUsernameLookup_prcNull() {
        final String value = strategy.apply(null);
        assertNull(value);
    }

    @Test
    public void testUsernameLookup_noSubjectContext() {
        final String value = strategy.apply(prc);
        assertNull(value);
    }

    @Test
    public void testUsernameLookup() {
        final SubjectContext subjectContext = prc.ensureSubcontext(SubjectContext.class);
        subjectContext.setPrincipalName("an-principal");
        final String value = strategy.apply(prc);
        assertNotNull(value);
        assertEquals(value, "an-principal");
    }

}
