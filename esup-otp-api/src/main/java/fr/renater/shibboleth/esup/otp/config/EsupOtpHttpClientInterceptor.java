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

package fr.renater.shibboleth.esup.otp.config;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Esup otp http client interceptor for add Authorization header.
 */
public class EsupOtpHttpClientInterceptor implements ClientHttpRequestInterceptor {
    
    /**
     * apiPassword to request esup-otp-api.
     */
    private final String apiPassword;
    
    /**
     * 
     * Constructor.
     *
     * @param apiPwd
     */
    public EsupOtpHttpClientInterceptor(final String apiPwd) {
        apiPassword = apiPwd;
    }

    /** {@inheritDoc} */
    public @Nonnull ClientHttpResponse intercept(@Nonnull final HttpRequest request, 
            @Nonnull final byte[] body, @Nonnull final ClientHttpRequestExecution execution)
            throws IOException {
        request.getHeaders().add("Authorization", "Bearer " + apiPassword);
        return execution.execute(request, body);
    }

}
