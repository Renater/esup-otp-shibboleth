package fr.renater.shibboleth.esup.otp.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.renater.shibboleth.esup.otp.config.EsupOtpMessageDeserializer;
import lombok.Data;

/**
 * Esup otp base response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class EsupOtpResponse {

    /**
     * Code.
     */
    private String code;
    
    /**
     * Message.
     */
    @JsonDeserialize(using = EsupOtpMessageDeserializer.class)
    private Object message;
    
}
