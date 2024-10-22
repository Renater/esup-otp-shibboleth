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

package fr.renater.shibboleth.esup.otp.client;

import fr.renater.shibboleth.esup.otp.dto.EsupOtpResponse;
import fr.renater.shibboleth.esup.otp.dto.EsupOtpUserInfoResponse;

/**
 * Esup otp api connector.
 */
public interface EsupOtpClient {
    
    /**
     * Otp
     */
    
    /**
     * 
     * @param uid
     * @return
     * @throws Exception
     */
    EsupOtpResponse getOtpUserInfos(String uid) throws Exception;
    
    /**
     * 
     * 
     * @param uid
     * @param method
     * @param transport
     * @param hash
     * @return
     * @throws Exception
     */
    EsupOtpResponse postSendMessage(String uid, String method, String transport, String hash) throws Exception;
    
    

    /**
     * Protected
     */
    
    /**
     * Get user infos.
     * 
     * @param uid
     * @return user infos
     */
    EsupOtpUserInfoResponse getUserInfos(String uid) throws Exception;

    /**
     * Get transport test.
     * 
     * @param uid
     * @param transport
     * @return esup otp api response
     */
    EsupOtpResponse getTransportTest(String uid, String transport) throws Exception;

    /**
     * Put activate with method.
     * 
     * @param uid
     * @param method
     * @return esup otp api response
     */
    EsupOtpResponse putActivate(String uid, String method) throws Exception;

    
    /**
     * @param uid
     * @param method
     * @return
     */
    EsupOtpResponse putDeactivate(String uid, String method) throws Exception;

    EsupOtpResponse postConfirmActivate(String uid, String method, String activationCode) throws Exception;

    EsupOtpResponse putUpdateTransport(String uid, String transport, String newTransport) throws Exception;

    EsupOtpResponse getNewTransportTest(String uid, String transport, String newTransport) throws Exception;

    EsupOtpResponse postSecret(String uid, String method) throws Exception;

    boolean postVerify(String uid, String otp) throws Exception;

    EsupOtpResponse deleteTransport(String uid, String transport) throws Exception;

}
