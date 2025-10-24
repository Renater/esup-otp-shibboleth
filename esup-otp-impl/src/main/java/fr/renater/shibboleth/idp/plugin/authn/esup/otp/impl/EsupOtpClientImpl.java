package fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import fr.renater.shibboleth.esup.otp.DefaultEsupOtpIntegration;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClientException;
import fr.renater.shibboleth.esup.otp.client.EsupOtpUriConstants;
import fr.renater.shibboleth.esup.otp.config.EsupOtpRestTemplate;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpVerifyResponse;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Esup otp api connector implementation.
 */
public class EsupOtpClientImpl extends AbstractEsupOtpConnector implements EsupOtpClient {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpClientImpl.class);

    /**
     * Constructor.
     *
     * @param integration DefaultEsupOtpIntegration.
     */
    public EsupOtpClientImpl(final DefaultEsupOtpIntegration integration) {
        super(new EsupOtpRestTemplate(integration));
    }

    /** {@inheritDoc} */
    public boolean postVerify(final String uid, final String otp) throws EsupOtpClientException {
        final EsupOtpVerifyResponse response = post(EsupOtpUriConstants.Protected.POST_VERIFY, 
                EsupOtpVerifyResponse.class, false, uid, otp);
        final boolean valid = response != null && response.getCode().equals("Ok");
        if(!valid) {
            log.info("Invalid token entered");
        }
        return valid;
    }

}
