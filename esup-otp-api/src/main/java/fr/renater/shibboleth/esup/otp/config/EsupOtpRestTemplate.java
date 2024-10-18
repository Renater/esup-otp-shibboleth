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

import javax.annotation.Nonnull;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import fr.renater.shibboleth.esup.otp.EsupOtpIntegration;


/**
 * Specific Rest template for esup otp.
 */
public class EsupOtpRestTemplate extends RestTemplate {

    /**
     * 
     * Constructor.
     *
     * @param esupOtpIntegration 
     */
    public EsupOtpRestTemplate(final EsupOtpIntegration esupOtpIntegration) {
        super();
        this.setUriTemplateHandler(new DefaultUriBuilderFactory(esupOtpIntegration.getAPIHost()));
        this.setRequestFactory(getClientHttpRequestFactory());
        this.getInterceptors().add(new EsupOtpHttpClientInterceptor(esupOtpIntegration.getApiPassword()));
    }
    
    private @Nonnull ClientHttpRequestFactory getClientHttpRequestFactory() {
        final int timeout = 5000;
        final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory 
            = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }

}
