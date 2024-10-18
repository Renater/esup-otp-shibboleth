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

import com.beust.jcommander.Parameter;

import net.shibboleth.idp.cli.AbstractIdPHomeAwareCommandLineArguments;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.codec.Base32Support;
import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintStream;

/**
 * Arguments for {@link EsupOtpAuthenticatorCLI}.
 */
public class EsupOtpAuthenticatorArguments extends AbstractIdPHomeAwareCommandLineArguments {

    /**
     * Name of a specific {@link TOTPAuthenticator}, if one has been requested.
     */
    @Parameter(names = "--authenticator")
    @Nullable private String authenticatorName;

    /** Credential issuer. */
    @Parameter(names = "--issuer")
    @Nullable private String issuer;
    
    /** Credential account name. */
    @Parameter(names = "--account")
    @Nullable private String account;
    
    /** method (bypass, esupnfc, push, random_code_mail, random_code, totp, webauthn). */
    @Parameter(names = "--method")
    @Nullable private String method;
    
    /** Chosen transport. */
    @Parameter(names = "--transport")
    @Nullable private String transport;
    
    /** Credential account name. */
    @Parameter(names = "--userHash")
    @Nullable private String userHash;
        
    /** Token code to verify. */
    @Nullable private Integer tokenCode;
    
    /** The Log. */
    @Nullable private Logger log;

    /** {@inheritDoc} */
    @Nonnull public Logger getLog() {
        if (log == null) {
            log = LoggerFactory.getLogger(EsupOtpAuthenticatorArguments.class);
        }
        assert log != null;
        return log;
    }
    
    /**
     * @return Returns the method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return Returns the transport.
     */
    public String getTransport() {
        return transport;
    }

    /**
     * @return Returns the userHash.
     */
    public String getUserHash() {
        return userHash;
    }

    /**
     * Get name of {@link TOTPAuthenticator} bean to access.
     * 
     * @return bean name
     */
    @Nullable @NotEmpty public String getAuthenticatorName() {
        return authenticatorName;
    }

    /**
     * Get the token issuer.
     * 
     * @return token issuer
     */
    @Nullable @NotEmpty public String getIssuer() {
        return StringSupport.trimOrNull(issuer);
    }

    /**
     * Get the token account name.
     * 
     * @return token account name
     */
    @Nullable @NotEmpty public String getAccountName() {
        return StringSupport.trimOrNull(account);
    }

    /**
     * Get token code to verify.
     * 
     * @return token code
     */
    @Nullable public Integer getTokenCode() {
        return tokenCode;
    }

    /** {@inheritDoc} */
    public void validate() throws IllegalArgumentException {
        super.validate();
        
        if (getOtherArgs().size() == 3) {
            tokenCode = Integer.valueOf(getOtherArgs().get(2));
        } else if (getOtherArgs().size() != 1) {
            throw new IllegalArgumentException(
                    "Invalid operation requested, must have zero or two additional arguments");
        }
    }

    /** {@inheritDoc} */
    public void printHelp(@Nonnull final PrintStream out) {
        out.println("TOTPAuthenticatorCLI");
        out.println("Provides a command line interface for TOTPAuthenticator operations.");
        out.println();
        out.println("   TOTPAuthenticatorCLI [options] springConfiguration [seed] [tokencode]");
        out.println();
        out.println("      springConfiguration      name of Spring configuration resource to use");
        out.println("      tokencode                token code to validate (omit when generating a new credential)");
        super.printHelp(out);
        out.println();
        out.println(String.format("  --%-20s %s", "authenticator",
                "Specifies a non-default TOTPAuthenticator bean to use."));
        out.println(String.format("  --%-20s %s", "issuer",
                "Specifies a token issuer when generating a new credential."));
        out.println(String.format("  --%-20s %s", "account",
                "Specifies a token account name when generating a new credential."));
        out.println();
    }

}