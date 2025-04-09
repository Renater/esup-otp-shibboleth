package fr.renater.shibboleth.esup.otp.config;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.renater.shibboleth.esup.otp.dto.MessageStatusResponse;

/**
 * EsupOtp custom deserializer.
 */
public class EsupOtpMessageDeserializer extends JsonDeserializer<Object> {
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) 
            throws IOException, JacksonException {
        if(p.currentToken() == JsonToken.VALUE_STRING) {
            return p.readValueAs(String.class);
        } else if(p.currentToken() == JsonToken.START_OBJECT) {
            return p.readValueAs(MessageStatusResponse.class);
        } else {
            throw JsonMappingException.from(p, "Unable to parse EsupOtpMessage response");
        }
    }
}
