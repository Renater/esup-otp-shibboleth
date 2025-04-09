package fr.renater.shibboleth.esup.otp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Esup otp verify dto response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper = true)
public class EsupOtpVerifyResponse extends EsupOtpResponse {

    /** Method response. */
    private String method;

}
