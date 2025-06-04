package fr.renater.shibboleth.esup.otp.dto.user;

import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Esup otp user response.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminEsupOtpUserInfoResponse extends EsupOtpResponse {

    /** user description. */
    private UserMethods user;
}
