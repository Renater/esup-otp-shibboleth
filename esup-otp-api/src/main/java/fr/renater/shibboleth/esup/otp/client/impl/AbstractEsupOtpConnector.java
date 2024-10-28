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

package fr.renater.shibboleth.esup.otp.client.impl;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import fr.renater.shibboleth.esup.otp.client.EsupOtpClientException;
import fr.renater.shibboleth.esup.otp.config.EsupOtpRestTemplate;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;

/**
 * Abstract esup otp client. 
 */
public abstract class AbstractEsupOtpConnector {
    
    
    /** Rest template. */
    private final EsupOtpRestTemplate restTemplate;
    
    /**
     * Constructor.
     *
     * @param esupOtpRestTemplate
     */
    public AbstractEsupOtpConnector(final EsupOtpRestTemplate esupOtpRestTemplate) {
        restTemplate = esupOtpRestTemplate;
    }
    
    protected <T extends EsupOtpResponse> T get(@Nonnull final String uri, 
            @Nonnull final Class<T> responseClass, @Nonnull final Object... uriVariables) 
                    throws EsupOtpClientException {
        try {
            
            final RequestEntity<?> request = RequestEntity
                    .get(uri, uriVariables)
                    .build();
            
            final ResponseEntity<T> response = restTemplate.exchange(request, responseClass);
            
            if(!response.getStatusCode().is2xxSuccessful()) {
                throw new EsupOtpClientException(
                        "Exception occured on call : " + uri + 
                        " with uri variables : " + Arrays.asList(uriVariables).stream()
                        .collect(Collectors.mapping(Object::toString, Collectors.joining(","))));
            }
            
            return response.getBody();
        }  catch (final RestClientException e) {
            throw new EsupOtpClientException("RestClientException occured on call: " + uri, e);
        }
    }
    
    protected <T extends EsupOtpResponse> T post(@Nonnull final String uri, 
            @Nonnull final Class<T> responseClass, @Nonnull final Object... uriVariables) 
                    throws EsupOtpClientException {
        try {
            
            final RequestEntity<?> request = RequestEntity
                    .post(uri, uriVariables)
                    .build();
            
            final ResponseEntity<T> response = restTemplate.exchange(request, responseClass);
            
            if(!response.getStatusCode().is2xxSuccessful()) {
                throw new EsupOtpClientException(
                        "Exception occured on call : " + uri + 
                        " with uri variables : " + Arrays.asList(uriVariables).stream()
                        .collect(Collectors.mapping(Object::toString, Collectors.joining(","))));
            }
            
            return response.getBody();
        }  catch (final RestClientException e) {
            throw new EsupOtpClientException("RestClientException occured on call: " + uri, e);
        }
    }
    
    protected void put(@Nonnull final String uri, @Nonnull final Object... uriVariables) throws EsupOtpClientException {
        try {
            
            final RequestEntity<?> request = RequestEntity
                    .put(uri, uriVariables)
                    .build();
            
            final ResponseEntity<Void> response = restTemplate.exchange(request, Void.class);
            
            if(!response.getStatusCode().is2xxSuccessful()) {
                throw new EsupOtpClientException(
                        "Exception occured on call : " + uri + 
                        " with uri variables : " + Arrays.asList(uriVariables).stream()
                        .collect(Collectors.mapping(Object::toString, Collectors.joining(","))));
            }
        }  catch (final RestClientException e) {
            throw new EsupOtpClientException("RestClientException occured on call: " + uri, e);
        }
    }
    
    protected <T extends EsupOtpResponse> T delete(@Nonnull final String uri, 
            @Nonnull final Class<T> responseClass, @Nonnull final Object... uriVariables) 
            throws EsupOtpClientException {
        try {
            
            final RequestEntity<?> request = RequestEntity
                    .delete(uri, uriVariables)
                    .build();
            
            final ResponseEntity<T> response = restTemplate.exchange(request, responseClass);
            
            if(!response.getStatusCode().is2xxSuccessful()) {
                throw new EsupOtpClientException(
                        "Exception occured on call : " + uri + 
                        " with uri variables : " + Arrays.asList(uriVariables).stream()
                        .collect(Collectors.mapping(Object::toString, Collectors.joining(","))));
            }
            
            return response.getBody();
        }  catch (final RestClientException e) {
            throw new EsupOtpClientException("RestClientException occured on call: " + uri, e);
        }
    }
}
