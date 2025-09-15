package fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * An Custom encoder for esup otp plugin.
 */
@ThreadSafe
public final class EsupOtpEncoder {

    /** Class logger. */
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(EsupOtpEncoder.class);

    @NotEmpty private String usersSecret;

    public EsupOtpEncoder() {
    }

    /** Constructor */
    public EsupOtpEncoder(@Nonnull @NotEmpty @ParameterName(name="usersSecret") String secret) {
        this.usersSecret = StringSupport.trimOrNull(secret);
    }

    public void setUsersSecret(@Nonnull @NotEmpty final String secret) {
        usersSecret = StringSupport.trimOrNull(secret);
    }

    /**
     * Compute user hash for request need it.
     * @param uid
     * @return user hash
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String getUserHash(final String uid) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        log.debug("Get user hash start");
        if (usersSecret == null) {
            log.warn("Users secret not configured");
            return null;
        }
        final MessageDigest md5Md = MessageDigest.getInstance("MD5");
        final String md5 = bytesToHex(md5Md.digest(usersSecret.getBytes())).toLowerCase();
        final String salt = md5 + getSalt(uid);
        final MessageDigest sha256Md = MessageDigest.getInstance("SHA-256");
        final String userHash = bytesToHex(sha256Md.digest(salt.getBytes())).toLowerCase();
        log.debug("Get user hash for {} = {}", uid, userHash);
        return userHash;
    }

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
