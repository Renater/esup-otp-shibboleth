package fr.renater.shibboleth.esup.otp.dto;

import lombok.Data;

/**
 * Dto when EsupOtpResponse message is an object.
 */
@Data
public class MessageStatusResponse {

    /** message id. */
    private Integer id;

    /** message status. */
    private String status;
}
