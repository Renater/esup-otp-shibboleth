package fr.renater.shibboleth.esup.otp;

import fr.renater.shibboleth.esup.otp.impl.EsupOtpAuthenticatorArguments;
import fr.renater.shibboleth.esup.otp.impl.EsupOtpAuthenticatorCLI;
import fr.renater.shibboleth.esup.otp.impl.EsupOtpExtractionAction;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.testing.ConstantSupplier;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EsupOtpAuthenticatorCLITest {


    private EsupOtpAuthenticatorCLI cli;

    @BeforeMethod
    public void setUp() throws ComponentInitializationException {

        cli = new EsupOtpAuthenticatorCLI();
    }

    @Test
    public void getArguments() {
        EsupOtpAuthenticatorArguments arguments = new EsupOtpAuthenticatorArguments();

        arguments.printHelp(System.out);
    }

    /*@Test
    public void getUserUids() {
        cli.get
    }*/
}
