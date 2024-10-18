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

package fr.renater.shibboleth.esup.otp.connector;

import javax.annotation.concurrent.ThreadSafe;

/**
 * An exception to signal an error condition during execution of a Esup Otp client.
 */
@ThreadSafe
public class EsupOtpClientInitializationException extends RuntimeException {
    
    /**
     * Default serialUID.
     */
    private static final long serialVersionUID = 279977852927336564L;

    /**
     * 
     * Constructor.
     *
     * @param message
     */
    public EsupOtpClientInitializationException(final String message) {
        super(message);
    }
    
    /**
     * 
     * Constructor.
     *
     * @param message
     * @param cause
     */
    public EsupOtpClientInitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
