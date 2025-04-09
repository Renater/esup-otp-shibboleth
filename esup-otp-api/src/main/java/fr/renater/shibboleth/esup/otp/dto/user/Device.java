package fr.renater.shibboleth.esup.otp.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Device dto.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device {

    /** platform. */
    private String platform;
    
    /** phone number. */
    @JsonProperty("phone_number")
    private String phoneNumber;
    
    /** manufacturer. */
    private String manufacturer;
    
    /** model. */
    private String model;
    
    
}
