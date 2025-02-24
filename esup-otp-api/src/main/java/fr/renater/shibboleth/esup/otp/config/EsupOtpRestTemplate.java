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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fr.renater.shibboleth.esup.otp.DefaultEsupOtpIntegration;


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
    public EsupOtpRestTemplate(final DefaultEsupOtpIntegration esupOtpIntegration) {
        super();
        this.setUriTemplateHandler(new DefaultUriBuilderFactory(esupOtpIntegration.getAPIHost()));
        this.setRequestFactory(new BufferingClientHttpRequestFactory(getClientHttpRequestFactory()));
        List<ClientHttpRequestInterceptor> interceptors = this.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new EsupOtpAuthInterceptor(esupOtpIntegration.getApiPassword(), esupOtpIntegration.getIssuer()));
        interceptors.add(new EsupOtpLoggingInterceptor());
        this.setInterceptors(interceptors);
        this.getMessageConverters().add(0, createMappingJacksonHttpMessageConverter());
    }
    
    private @Nonnull ClientHttpRequestFactory getClientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory clientHttpRequestFactory 
            = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient());
        return clientHttpRequestFactory;
    }

    private @Nonnull CloseableHttpClient httpClient() {
        final CloseableHttpClient closeableHttpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig())
                .evictExpiredConnections()
                .evictIdleConnections(TimeValue.of(5000, TimeUnit.MILLISECONDS))
                .setRetryStrategy(new RetryOverHttpError())
                .setConnectionManager(poolingHttpClientConnectionManager())
                .build();
        assert closeableHttpClient != null;
        return closeableHttpClient;
    }

    private RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
    }

    private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        final ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(5000, TimeUnit.MILLISECONDS)
                .build();
        connectionManager.setDefaultConnectionConfig(connectionConfig);
        return connectionManager;
    }
    
    private MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {

        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(createObjectMapper());
        return converter;
    }
    
    private @Nonnull ObjectMapper createObjectMapper() {

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return objectMapper;
   }

    @Override
    protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
        ClientHttpRequest request = getRequestFactory().createRequest(url, method);
        getClientHttpRequestInitializers().forEach(initializer -> initializer.initialize(request));
        return request;
    }
}
