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

import javax.annotation.Nonnull;

import org.apache.hc.core5.http.NotImplementedException;
import org.slf4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import fr.renater.shibboleth.esup.otp.EsupOtpIntegration;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClientException;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import fr.renater.shibboleth.esup.otp.client.EsupOtpUriConstants;
import fr.renater.shibboleth.esup.otp.config.EsupOtpRestTemplate;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpUserInfoResponse;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Esup otp api connector implementation.
 */
public class EsupOtpConnectorImpl implements EsupOtpClient {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpConnectorImpl.class);
    
    private final EsupOtpRestTemplate restTemplate;

    public EsupOtpConnectorImpl(final EsupOtpRestTemplate esupOtpRestTemplate) {
        restTemplate = esupOtpRestTemplate;
    }
    
    /** {@inheritDoc} */
    public EsupOtpResponse getOtpUserInfos(String uid) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public EsupOtpResponse postSendMessage(String uid, String method, String transport, String hash) throws Exception {
        try {
            log.info("get user infos start");
            final ResponseEntity<EsupOtpUserInfoResponse> response =
                    restTemplate.getForEntity(EsupOtpUriConstants.GET_USER_INFOS_URI, EsupOtpUserInfoResponse.class,
                            uid);
            if(response.getStatusCode().is2xxSuccessful()) {
                log.info("get user infos success");
                return response.getBody();
            }
            log.info("get user infos error");
            throw new EsupOtpClientException("Exception occured on get user infos for uid: " + uid);
        } catch (final RestClientException e) {
            throw new EsupOtpClientException("RestClientException occured on get user infos for uid: " + uid, e);
        }
    }

    /** {@inheritDoc} */
    public EsupOtpUserInfoResponse getUserInfos(final String uid) throws Exception {
        try {
            log.info("get user infos start");
            final ResponseEntity<EsupOtpUserInfoResponse> response =
                    restTemplate.getForEntity(EsupOtpUriConstants.GET_USER_INFOS_URI, EsupOtpUserInfoResponse.class,
                            uid);
            if(response.getStatusCode().is2xxSuccessful()) {
                log.info("get user infos success");
                return response.getBody();
            }
            log.info("get user infos error");
            throw new EsupOtpClientException("Exception occured on get user infos for uid: " + uid);
        } catch (final RestClientException e) {
            throw new EsupOtpClientException("RestClientException occured on get user infos for uid: " + uid, e);
        }
    }

    /** {@inheritDoc} */
    public EsupOtpResponse getTransportTest(final String uid, final String transport) throws Exception {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    public EsupOtpResponse putActivate(final String uid, final String method) throws Exception {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    public EsupOtpResponse putDeactivate(final String uid, final String method) throws Exception {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    public EsupOtpResponse postConfirmActivate(final String uid, final String method, final String activationCode)
            throws Exception {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    public EsupOtpResponse putUpdateTransport(final String uid, final String transport, final String newTransport)
            throws Exception {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    public EsupOtpResponse getNewTransportTest(final String uid, final String transport, final String newTransport)
            throws Exception {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    public EsupOtpResponse postSecret(final String uid, final String method) throws Exception {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    public boolean postVerify(final String uid, final String otp) throws Exception {
        try {
            log.info("verify token start");
            final ResponseEntity<EsupOtpResponse> response = 
                    restTemplate.postForEntity(EsupOtpUriConstants.POST_VERIFY_URI, null, EsupOtpResponse.class,
                            uid, otp);
            if(response.getStatusCode().is2xxSuccessful()) {
                log.info("verify token success");
                final EsupOtpResponse body = response.getBody();
                final boolean result = body != null ? body.getCode().equals("Ok") : false;
                if(!result) {
                    log.info("Invalid token entered");
                }
                return result;
            }
            log.info("verify token error");
            throw new EsupOtpClientException("Exception occured on get user infos for uid: " + uid);
        } catch (final RestClientException e) {
            throw new EsupOtpClientException("RestClientException occured on get user infos for uid: " + uid, e);
        }
    }

    /** {@inheritDoc} */
    public EsupOtpResponse deleteTransport(final String uid, final String transport) throws Exception {
        throw new NotImplementedException();
    }

}
