package fr.renater.shibboleth.esup.otp;

import fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl.EsupOtpAuthenticatorArguments;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl.EsupOtpAuthenticatorCLI;
import net.shibboleth.shared.component.ComponentInitializationException;
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
