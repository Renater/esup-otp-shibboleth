/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.renater.shibboleth.esup.otp.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.renater.shibboleth.esup.otp.DefaultEsupOtpIntegration;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import fr.renater.shibboleth.esup.otp.client.impl.EsupOtpClientImpl;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpUsersResponse;
import fr.renater.shibboleth.esup.otp.dto.user.EsupOtpUserInfoResponse;
import net.shibboleth.idp.cli.AbstractIdPHomeAwareCommandLine;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory;

import java.io.PrintStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Command line utility for {@link EsupOtpClient}.
 */
public class EsupOtpAuthenticatorCLI extends AbstractIdPHomeAwareCommandLine<EsupOtpAuthenticatorArguments> {

    /** Class logger. */
    @Nullable private Logger log;
    
    private final ObjectMapper objectMapper;

    public EsupOtpAuthenticatorCLI() {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper = objMapper;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull protected Logger getLogger() {
        if (log == null) {
            log = LoggerFactory.getLogger(EsupOtpAuthenticatorCLI.class);
        }
        assert log != null;
        return log;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull protected Class<EsupOtpAuthenticatorArguments> getArgumentClass() {
        return EsupOtpAuthenticatorArguments.class;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull @NotEmpty protected String getVersion() {
        final String result = getClass().getPackage().getImplementationVersion();
        assert result != null;
        return result;
    }
    
    /** {@inheritDoc} */
    @Override
    protected int doRun(@Nonnull final EsupOtpAuthenticatorArguments args) {
        final int ret = super.doRun(args);
        if (ret != RC_OK) {
            return ret;
        }
        
        try {
            DefaultEsupOtpIntegration integration = getApplicationContext().getBean(DefaultEsupOtpIntegration.class);

            final EsupOtpClient client = new EsupOtpClientRegistry().getClientOrCreate(integration);
            
            if("all".equals(args.getCommand())) {
                final EsupOtpUsersResponse userUids = client.getUsers();
                final String response = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userUids);
                System.out.println("EsupOtpUsersResponse: \n" + response);
                return RC_OK;
            } else if ("protected".equals(args.getCommand()) && args.getUid() != null) {
                System.out.println("Get Protected user infos");
                final EsupOtpUserInfoResponse user = client.getUserInfos(args.getUid());
                final String response = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
                System.out.println("EsupOtpUserInfoResponse: \n" + response);
                return RC_OK;
            } else if (args.getOtherArgs().size() == 1 && args.getUid() != null) {
                final EsupOtpUserInfoResponse user = client.getOtpUserInfos(args.getUid());
                final String response = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
                System.out.println("EsupOtpUserInfoResponse: \n" + response);
                return RC_OK;
            } else {
                final String username = args.getUid();
                final Integer tokenCode = args.getTokenCode();
                if (username != null && tokenCode != null) {

                    if (client.postVerify(username, tokenCode.toString())) {
                        System.out.println("OK");
                        return RC_OK;
                    }
                    
                    System.out.println("INVALID");
                    return RC_UNKNOWN;
                }
                
                final String method = args.getMethod() != null ? args.getMethod() : "totp";
                final String transport = args.getTransport() != null ? args.getTransport() : "sms";
                
                // Create a new token.
                final EsupOtpResponse tc = client.postSendMessage(username, method, transport, "");
                System.out.println("Send message code: " + tc.getCode());
                System.out.println("Send message message: " + tc.getMessage());
            }
            
        } catch (final Exception e) {
            if (args.isVerboseOutput()) {
                getLogger().error("Unable to access EsupOtpAuthenticator from Spring context", e);
            } else {
                getLogger().error("Unable to access EsupOtpAuthenticator from Spring context", e.getMessage());
            }
            return RC_UNKNOWN;
        }
        
        return RC_OK;
    }

    /**
     * CLI entry point.
     * 
     * @param args arguments
     */
    public static void main(@Nonnull final String[] args) {
        System.exit(new EsupOtpAuthenticatorCLI().run(args));
    }
    
}