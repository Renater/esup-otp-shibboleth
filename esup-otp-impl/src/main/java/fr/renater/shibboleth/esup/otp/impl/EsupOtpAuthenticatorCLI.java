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

package net.shibboleth.idp.plugin.authn.totp.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.google.common.net.UrlEscapers;

import net.shibboleth.idp.cli.AbstractIdPHomeAwareCommandLine;
import net.shibboleth.idp.plugin.authn.totp.impl.TOTPAuthenticator.TOTPCredential;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.codec.Base32Support;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Command line utility for {@link TOTPAuthenticator}.
 */
public class TOTPAuthenticatorCLI extends AbstractIdPHomeAwareCommandLine<TOTPAuthenticatorArguments> {

    /** Class logger. */
    @Nullable private Logger log;
    
    /** {@inheritDoc} */
    @Override
    @Nonnull protected Logger getLogger() {
        if (log == null) {
            log = LoggerFactory.getLogger(TOTPAuthenticatorCLI.class);
        }
        assert log != null;
        return log;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nonnull protected Class<TOTPAuthenticatorArguments> getArgumentClass() {
        return TOTPAuthenticatorArguments.class;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull @NotEmpty protected String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }
    
    /** {@inheritDoc} */
    @Override
    protected int doRun(@Nonnull final TOTPAuthenticatorArguments args) {
        final int ret = super.doRun(args);
        if (ret != RC_OK) {
            return ret;
        }
        
        try {
            final TOTPAuthenticator authenticator;
            final String authenticatorName = args.getAuthenticatorName();
            if (authenticatorName != null) {
                authenticator = getApplicationContext().getBean(authenticatorName, TOTPAuthenticator.class);
            } else {
                authenticator = getApplicationContext().getBean(TOTPAuthenticator.class);
            }
            
            final byte[] seed = args.getSeed();
            final Integer tokenCode = args.getTokenCode();
            if (seed != null && tokenCode != null) {

                if (authenticator.validate(seed, tokenCode)) {
                    System.out.println("OK");
                    return RC_OK;
                }
                
                System.out.println("INVALID");
                return RC_UNKNOWN;
            }
            
            // Create a new token.
            final TOTPCredential tc = authenticator.createCredential(args.getIssuer(), args.getAccountName());
            System.out.println("Seed: " + Base32Support.encode(tc.getKey(), false));
            System.out.println("URL: " + tc.getTOTPURL());
            System.out.println("QR Code: https://api.qrserver.com/v1/create-qr-code/?data=" +
                    UrlEscapers.urlFormParameterEscaper().escape(tc.getTOTPURL()) + "&size=200x200&ecc=M&margin=0");
            
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
        System.exit(new TOTPAuthenticatorCLI().run(args));
    }
    
}