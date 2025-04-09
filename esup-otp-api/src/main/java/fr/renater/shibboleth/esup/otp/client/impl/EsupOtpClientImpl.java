package fr.renater.shibboleth.esup.otp.client.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import fr.renater.shibboleth.esup.otp.DefaultEsupOtpIntegration;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClientException;
import fr.renater.shibboleth.esup.otp.client.EsupOtpUriConstants;
import fr.renater.shibboleth.esup.otp.config.EsupOtpRestTemplate;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpUsersResponse;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpVerifyResponse;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpVerifyWebAuthnRequest;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpVerifyWebAuthnResponse;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpWebauthnResponse;
import fr.renater.shibboleth.esup.otp.dto.user.EsupOtpUserInfoResponse;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Esup otp api connector implementation.
 */
public class EsupOtpClientImpl extends AbstractEsupOtpConnector implements EsupOtpClient {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpClientImpl.class);

    /** Esup otp integration configuration. */
    private final DefaultEsupOtpIntegration esupOtpIntegration;
    
    /**
     * Constructor.
     *
     * @param integration DefaultEsupOtpIntegration.
     */
    public EsupOtpClientImpl(final DefaultEsupOtpIntegration integration) {
        super(new EsupOtpRestTemplate(integration));

        this.esupOtpIntegration = integration;
    }
   
    
    /** {@inheritDoc} */
    public EsupOtpUserInfoResponse getOtpUserInfos(final String uid) throws EsupOtpClientException {
        try {
            final String hash = getUserHash(uid);
            return get(EsupOtpUriConstants.Public.GET_USER_INFOS, EsupOtpUserInfoResponse.class, uid, hash);
        } catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new EsupOtpClientException("Get user hash failed", e);
        }

    }

    /** {@inheritDoc} */
    public EsupOtpResponse postSendMessage(final String uid, final String method,
                                                  final String transport) throws EsupOtpClientException {
        try {
            final String hash = getUserHash(uid);
            return post(EsupOtpUriConstants.Public.POST_MESSAGE, EsupOtpResponse.class,
                    true, uid, method, transport, hash);
        } catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new EsupOtpClientException("Get user hash failed", e);
        }
    }

    @Override
    public EsupOtpWebauthnResponse postGenerateWebauthnSecret(final String uid) throws EsupOtpClientException {
        try {
            final String hash = getUserHash(uid);
            return post(EsupOtpUriConstants.Public.POST_GENERATE_WEBAUTHN, EsupOtpWebauthnResponse.class,
                    true, uid, hash);
        } catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new EsupOtpClientException("Get user hash failed", e);
        }
    }

    /** {@inheritDoc} */
    public EsupOtpUserInfoResponse getUserInfos(final String uid) throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Protected.GET_USER_INFOS, EsupOtpUserInfoResponse.class, uid);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse getTransportTest(final String uid, final String transport) throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Protected.GET_TRANSPORT_TEST, EsupOtpResponse.class, uid, transport);
    }

    /** {@inheritDoc} */
    public void putActivate(final String uid, final String method) throws EsupOtpClientException {
        put(EsupOtpUriConstants.Protected.PUT_ACTIVATE, uid, method);
    }

    /** {@inheritDoc} */
    public void putDeactivate(final String uid, final String method) throws EsupOtpClientException {
        put(EsupOtpUriConstants.Protected.PUT_DEACTIVATE, uid, method);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse postConfirmActivate(final String uid, final String method, final String activationCode)
            throws EsupOtpClientException {
        return post(EsupOtpUriConstants.Protected.POST_CONFIRM_ACTIVATE, EsupOtpResponse.class, 
                true, uid, method, activationCode);
    }

    /** {@inheritDoc} */
    public void putUpdateTransport(final String uid, final String transport, final String newTransport)
            throws EsupOtpClientException {
        put(EsupOtpUriConstants.Protected.PUT_UPDATE_TRANSPORT, uid, transport, newTransport);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse getNewTransportTest(final String uid, final String transport, final String newTransport)
            throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Protected.GET_NEW_TRANSPORT_TEST, EsupOtpResponse.class, 
                uid, transport, newTransport);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse postSecret(final String uid, final String method) throws EsupOtpClientException {
        return post(EsupOtpUriConstants.Protected.POST_SECRET, EsupOtpResponse.class, true, uid, method);
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

    /** {@inheritDoc} */
    public boolean postVerifyWebauthn(final String uid, @Nonnull final EsupOtpVerifyWebAuthnRequest body) 
            throws EsupOtpClientException {
        try {
            final String hash = getUserHash(uid);
            final RequestEntity<?> request = RequestEntity
                    .post(EsupOtpUriConstants.Public.POST_VERIFY_WEBAUTHN, uid, hash)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body);

            final ResponseEntity<EsupOtpVerifyWebAuthnResponse> response =
                    getRestTemplate().exchange(request, EsupOtpVerifyWebAuthnResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            }

            throw new EsupOtpClientException(
                    "Exception occured on call : " + EsupOtpUriConstants.Public.POST_VERIFY_WEBAUTHN +
                            " with uri variables : " + uid);
        } catch (final NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new EsupOtpClientException("Get user hash failed", e);
        } catch (final RestClientException e) {
            throw new EsupOtpClientException("RestClientException occured on call: " + 
                    EsupOtpUriConstants.Public.POST_VERIFY_WEBAUTHN, e);
        }
    }

    /** {@inheritDoc} */
    public EsupOtpResponse deleteTransport(final String uid, final String transport) throws EsupOtpClientException {
        return delete(EsupOtpUriConstants.Protected.DELETE_TRANSPORT, EsupOtpResponse.class, uid, transport);
    }

    /** {@inheritDoc} */
    public EsupOtpUsersResponse getUsers() throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Admin.GET_USERS, EsupOtpUsersResponse.class);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse getUser(final String uid) throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Admin.GET_USER, EsupOtpResponse.class, uid);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse getMethods(final String uid) throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Admin.GET_METHODS, EsupOtpResponse.class, uid);
    }

    /** {@inheritDoc} */
    public void putActivateMethodTransport(final String method, final String transport) throws EsupOtpClientException{
        put(EsupOtpUriConstants.Admin.PUT_ACTIVATE_TRANSPORT, method, transport);
    }

    /** {@inheritDoc} */
    public void putDeactivateMethodTransport(final String method, final String transport) 
            throws EsupOtpClientException {
        put(EsupOtpUriConstants.Admin.PUT_DEACTIVATE_TRANSPORT, method, transport);
    }

    /** {@inheritDoc} */
    public void putActivateMethod(final String method) throws EsupOtpClientException {
        put(EsupOtpUriConstants.Admin.PUT_ACTIVATE, method);
    }

    /** {@inheritDoc} */
    public void putDeactivateMethod(final String method) throws EsupOtpClientException {
        put(EsupOtpUriConstants.Admin.PUT_DEACTIVATE_TRANSPORT, method);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse deleteMethodSecret(final String uid, final String method) throws EsupOtpClientException {
        return delete(EsupOtpUriConstants.Admin.DELETE_SECRET, EsupOtpResponse.class, uid, method);
    }

    /**
     * Génère un hash unique pour un utilisateur donné en utilisant son identifiant (UID).
     *
     * @param uid l'identifiant unique de l'utilisateur pour lequel le hash doit être généré.
     * @return une chaîne représentant le hash de l'UID.
     * @throws NoSuchAlgorithmException si l'algorithme de hachage spécifié n'est pas pris en charge.
     * @throws UnsupportedEncodingException si le codage utilisé pour convertir les données n'est pas pris en charge.
     */
    private String getUserHash(final String uid) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest md5Md = MessageDigest.getInstance("MD5");
        final String md5 = bytesToHex(md5Md.digest(esupOtpIntegration.getUsersSecret().getBytes())).toLowerCase();
        final String salt = md5 + getSalt(uid);
        final MessageDigest sha256Md = MessageDigest.getInstance("SHA-256");
        return bytesToHex(sha256Md.digest(salt.getBytes())).toLowerCase();
    }

    /**
     * Convertit un tableau d'octets en une représentation hexadécimale sous forme de chaîne de caractères.
     *
     * @param bytes le tableau d'octets à convertir.
     * @return une chaîne représentant les octets sous forme hexadécimale.
     */
    private String bytesToHex(final byte[] bytes) {
        final StringBuilder hexString = new StringBuilder();
        for (final byte b : bytes) {
            final String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Génère ou récupère une valeur de "sel" (salt) associée à un identifiant utilisateur (UID).
     * <p>
     * Le "sel" est utilisé pour renforcer le hachage en ajoutant une donnée unique ou aléatoire,
     * rendant le hachage plus résistant aux attaques par dictionnaire ou par table arc-en-ciel.
     * </p>
     *
     * @param uid l'identifiant unique de l'utilisateur pour lequel le sel est requis.
     * @return une chaîne représentant le sel associé à l'UID.
     */
    private String getSalt(final String uid) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return uid + day + hour;
    }

}
