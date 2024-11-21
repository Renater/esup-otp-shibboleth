package fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.renater.shibboleth.idp.plugin.authn.esup.otp.dto.WebAuthnDto;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;

public final class EsupOtpEncoder {

    /** Class logger. */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(EsupOtpEncoder.class);

    private static String usersSecret;

    /** Private constructor */
    private EsupOtpEncoder() {
        // Do nothing
    }

    public static ObjectMapper getWebAuthnObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        objectMapper.setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper;
    }

    /**
     * Serialize the PublicKeyCredentialRequestOptions request into a JSON string.
     *
     * @param options the options to serialize
     *
     * @return the JSON serialized PublicKeyCredentialRequestOptions, or an empty string if there is an error
     *          converting the string.
     */
    public static String serializePublicKeyCredentialRequestOptionsAsJSON(
            @Nullable final WebAuthnDto options) {
        log.debug("Get options : {}", options);
        if (options != null) {
            try {
                ObjectMapper objectMapper = getWebAuthnObjectMapper();
                ObjectNode result = objectMapper.createObjectNode();
                result.set("publicKey", objectMapper.valueToTree(options));
                return objectMapper.writeValueAsString(result);
            } catch (final JsonProcessingException e) {
                log.debug("Unable to serialize PublicKeyCredentialOptions", e);
            }
        }
        return "";
    }

    /**
     * Compute user hash for request need it.
     * @param uid
     * @return user hash
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    /*public static String getUserHash(final String uid) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest md5Md = MessageDigest.getInstance("MD5");
        final String md5 = bytesToHex(md5Md.digest(esupOtpIntegration.getUsersSecret().getBytes())).toLowerCase();
        final String salt = md5 + getSalt(uid);
        final MessageDigest sha256Md = MessageDigest.getInstance("SHA-256");
        final String userHash = bytesToHex(sha256Md.digest(salt.getBytes())).toLowerCase();
        return userHash;
    }*/

    /**
     * Convert bytes array to hexadecimal string.
     * @param bytes
     * @return hexadecimal string.
     */
    private static String bytesToHex(final byte[] bytes) {
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
     * Get salt for uid.
     * @param uid
     * @return salt.
     */
    public static String getSalt(final String uid) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final String salt = uid + day + hour;
        return salt;
    }
}
