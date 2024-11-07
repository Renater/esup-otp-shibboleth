package fr.renater.shibboleth.esup.otp.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class EsupOtpMessageDeserializerTest {

    @Test
    public final void deserializeMessageString() throws JsonParseException, JsonMappingException, IOException {
        final String json = "{\n" +
                "    \"code\": \"Ok\",\n" +
                "    \"message\": \"message\"" +
                "}";

        final EsupOtpResponse readValue = new ObjectMapper().readValue(json, EsupOtpResponse.class);
        Assert.assertNotNull(readValue);
    }

    @Test
    public final void deserializeMessageObject() throws JsonParseException, JsonMappingException, IOException {
        final String json = "{\n" +
                "    \"code\": \"Ok\",\n" +
                "    \"message\": {\n" +
                "        \"id\": 715,\n" +
                "        \"status\": \"OK\"\n" +
                "    }\n" +
                "}";

        final EsupOtpResponse readValue = new ObjectMapper().readValue(json, EsupOtpResponse.class);
        Assert.assertNotNull(readValue);
    }

    @Test
    public final void deserializeMessageArray_exception() throws JsonParseException, JsonMappingException, IOException {
        final String json = "{\n" +
                "    \"code\": \"Ok\",\n" +
                "    \"message\": [{\n" +
                "        \"id\": 715,\n" +
                "        \"status\": \"OK\"\n" +
                "    }]\n" +
                "}";

        Assert.assertThrows(JsonMappingException.class, () -> new ObjectMapper().readValue(json, EsupOtpResponse.class));
    }
}
