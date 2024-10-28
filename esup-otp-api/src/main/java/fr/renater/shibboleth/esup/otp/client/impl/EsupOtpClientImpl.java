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

import org.slf4j.Logger;

import fr.renater.shibboleth.esup.otp.client.EsupOtpClientException;
import fr.renater.shibboleth.esup.otp.client.EsupOtpClient;
import fr.renater.shibboleth.esup.otp.client.EsupOtpUriConstants;
import fr.renater.shibboleth.esup.otp.config.EsupOtpRestTemplate;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpUsersResponse;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpVerifyResponse;
import fr.renater.shibboleth.esup.otp.dto.user.EsupOtpUserInfoResponse;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Esup otp api connector implementation.
 */
public class EsupOtpClientImpl extends AbstractEsupOtpConnector implements EsupOtpClient {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EsupOtpClientImpl.class);
    
    /**
     * Constructor.
     *
     * @param esupOtpRestTemplate
     */
    public EsupOtpClientImpl(final EsupOtpRestTemplate esupOtpRestTemplate) {
        super(esupOtpRestTemplate);
    }
   
    
    /** {@inheritDoc} */
    public EsupOtpUserInfoResponse getOtpUserInfos(final String uid) throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Public.GET_USER_INFOS, EsupOtpUserInfoResponse.class, uid);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse postSendMessage(final String uid, final String method, 
            final String transport, final String hash) throws EsupOtpClientException {
        return post(EsupOtpUriConstants.Public.POST_MESSAGE, EsupOtpUserInfoResponse.class, 
                uid, method, transport, hash);
    }

    /** {@inheritDoc} */
    public EsupOtpUserInfoResponse getUserInfos(final String uid) throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Protected.GET_USER_INFOS, EsupOtpUserInfoResponse.class, uid);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse getTransportTest(final String uid, final String transport) throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Protected.GET_TRANSPORT_TEST, EsupOtpResponse.class, uid, transport);
    }

    /** {@inheritDoc} */
    public void putActivate(final String uid, final String method) throws EsupOtpClientException {
        put(EsupOtpUriConstants.Protected.PUT_ACTIVATE, uid, method);
    }

    /** {@inheritDoc} */
    public void putDeactivate(final String uid, final String method) throws EsupOtpClientException {
        put(EsupOtpUriConstants.Protected.PUT_DEACTIVATE, uid, method);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse postConfirmActivate(final String uid, final String method, final String activationCode)
            throws EsupOtpClientException {
        return post(EsupOtpUriConstants.Protected.POST_CONFIRM_ACTIVATE, EsupOtpResponse.class, 
                uid, method, activationCode);
    }

    /** {@inheritDoc} */
    public void putUpdateTransport(final String uid, final String transport, final String newTransport)
            throws EsupOtpClientException {
        put(EsupOtpUriConstants.Protected.PUT_UPDATE_TRANSPORT, uid, transport, newTransport);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse getNewTransportTest(final String uid, final String transport, final String newTransport)
            throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Protected.GET_NEW_TRANSPORT_TEST, EsupOtpResponse.class, 
                uid, transport, newTransport);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse postSecret(final String uid, final String method) throws EsupOtpClientException {
        return post(EsupOtpUriConstants.Protected.POST_SECRET, EsupOtpResponse.class, uid, method);
    }

    /** {@inheritDoc} */
    public boolean postVerify(final String uid, final String otp) throws EsupOtpClientException {
        final EsupOtpVerifyResponse response = post(EsupOtpUriConstants.Protected.POST_VERIFY, EsupOtpVerifyResponse.class, 
                uid, otp);
        final boolean result = response != null ? response.getCode().equals("Ok") : false;
        if(!result) {
            log.info("Invalid token entered");
        }
        return result;
    }

    /** {@inheritDoc} */
    public EsupOtpResponse deleteTransport(final String uid, final String transport) throws EsupOtpClientException {
        return delete(EsupOtpUriConstants.Protected.DELETE_TRANSPORT, EsupOtpResponse.class, uid, transport);
    }

    /** {@inheritDoc} */
    public EsupOtpUsersResponse getUsers() throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Admin.GET_USERS, EsupOtpUsersResponse.class);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse getUser(final String uid) throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Admin.GET_USER, EsupOtpResponse.class, uid);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse getMethods(final String uid) throws EsupOtpClientException {
        return get(EsupOtpUriConstants.Admin.GET_METHODS, EsupOtpResponse.class, uid);
    }

    /** {@inheritDoc} */
    public void putActivateMethodTransport(final String method, final String transport) throws EsupOtpClientException{
        put(EsupOtpUriConstants.Admin.PUT_ACTIVATE_TRANSPORT, method, transport);
    }

    /** {@inheritDoc} */
    public void putDeactivateMethodTransport(final String method, final String transport) 
            throws EsupOtpClientException {
        put(EsupOtpUriConstants.Admin.PUT_DEACTIVATE_TRANSPORT, method, transport);
    }

    /** {@inheritDoc} */
    public void putActivateMethod(final String method) throws EsupOtpClientException {
        put(EsupOtpUriConstants.Admin.PUT_ACTIVATE, method);
    }

    /** {@inheritDoc} */
    public void putDeactivateMethod(final String method) throws EsupOtpClientException {
        put(EsupOtpUriConstants.Admin.PUT_DEACTIVATE_TRANSPORT, method);
    }

    /** {@inheritDoc} */
    public EsupOtpResponse deleteMethodSecret(final String uid, final String method) throws EsupOtpClientException {
        return delete(EsupOtpUriConstants.Admin.DELETE_SECRET, EsupOtpResponse.class, uid, method);
    }

}
