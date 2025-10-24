package fr.renater.shibboleth.esup.otp.client;

/**
 * Esup otp api connector.
 */
public interface EsupOtpClient {
    
    //----------------------------------------------------------------
    // Protected calls.
    //----------------------------------------------------------------
    
    /**
     * Verifies the user's identity using an OTP (one-time password).
     * 
     * @param uid The unique identifier of the user.
     * @param otp The one-time password used for verification.
     * @return boolean Returns true if verification is successful, otherwise false.
     * @throws EsupOtpClientException If an error occurs during the verification process.
     */
    boolean postVerify(String uid, String otp) throws EsupOtpClientException;

}
