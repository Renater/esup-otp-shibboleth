package fr.renater.shibboleth.esup.otp.client;

/**
 * Esup otp api constants.
 */
public final class EsupOtpUriConstants {
    
    /**
     * Protected uris.
     */
    public final class Protected {
        
        /**
         * GET request to retrieve detailed information of a specific user.
         * 
         * Endpoint: /protected/users/{uid}
         * 
         * @param uid The unique identifier of the user whose information is being requested.
         */
        public static final String GET_USER_INFOS = "/protected/users/{uid}";

        /**
         * POST request to verify the user with an OTP and API password.
         * 
         * Endpoint: /protected/users/{uid}/{otp}/{api_password}
         * 
         * @param uid The unique identifier of the user.
         * @param otp The one-time password for verification.
         * @param api_password The API password for additional security.
         */
        public static final String POST_VERIFY = "/protected/users/{uid}/{otp}";

        /**
         * Constructor.
         *
         */
        private Protected() {
            
        }
        
    }
    
    private EsupOtpUriConstants() {
        
    }
    
}
