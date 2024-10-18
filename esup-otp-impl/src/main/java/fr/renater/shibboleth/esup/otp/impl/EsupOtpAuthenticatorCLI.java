/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import fr.renater.shibboleth.esup.otp.connector.EsupOtpConnector;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;
import net.shibboleth.idp.cli.AbstractIdPHomeAwareCommandLine;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Command line utility for {@link EsupOtpConnector}.
 */
public class EsupOtpAuthenticatorCLI extends AbstractIdPHomeAwareCommandLine<EsupOtpAuthenticatorArguments> {

    /** Class logger. */
    @Nullable private Logger log;
    
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
        return getClass().getPackage().getImplementationVersion();
    }
    
    /** {@inheritDoc} */
    @Override
    protected int doRun(@Nonnull final EsupOtpAuthenticatorArguments args) {
        final int ret = super.doRun(args);
        if (ret != RC_OK) {
            return ret;
        }
        
        try {
            final EsupOtpConnector authenticator;
            final String authenticatorName = args.getAuthenticatorName();
            if (authenticatorName != null) {
                authenticator = getApplicationContext().getBean(authenticatorName, EsupOtpConnector.class);
            } else {
                authenticator = getApplicationContext().getBean(EsupOtpConnector.class);
            }

            final String username = args.getAccountName();
            final Integer tokenCode = args.getTokenCode();
            if (username != null && tokenCode != null) {

                if (authenticator.postVerify(username, tokenCode.toString())) {
                    System.out.println("OK");
                    return RC_OK;
                }
                
                System.out.println("INVALID");
                return RC_UNKNOWN;
            }
            
            final String method = args.getMethod() != null ? args.getMethod() : "totp";
            final String transport = args.getTransport() != null ? args.getTransport() : "sms";
            
            // Create a new token.
            final EsupOtpResponse tc = authenticator.postSendMessage(username, method, transport, "");
            System.out.println("Send message code: " + tc.getCode());
            System.out.println("Send message message: " + tc.getMessage());
            
        } catch (final Exception e) {
            if (args.isVerboseOutput()) {
                getLogger().error("Unable to access TOTPAuthenticator from Spring context", e);
            } else {
                getLogger().error("Unable to access TOTPAuthenticator from Spring context", e.getMessage());
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