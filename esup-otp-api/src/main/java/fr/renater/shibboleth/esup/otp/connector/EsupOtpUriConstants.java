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

/**
 * Esup otp api constants.
 */
public final class EsupOtpUriConstants {
    
    public static final String GET_USER_INFOS_URI = "/protected/users/{uid}";

    public static final String GET_TRANSPORT_TEST_URI = "/protected/users/{uid}/transports/{transport}/test";
    
    public static final String PUT_ACTIVATE_URI = "/protected/users/{uid}/methods/{method}/activate";
    
    public static final String PUT_DEACTIVATE_URI = "/protected/users/:uid/methods/:method/deactivate";
    
    public static final String POST_CONFIRM_ACTIVATE_URI = 
            "/protected/users/:uid/methods/:method/activate/:activation_code";
    
    public static final String PUT_UPDATE_TRANSPORT_URI = "/protected/users/:uid/transports/:transport/:new_transport";
    
    public static final String GET_NEW_TRANSPORT_TEST_URI = 
            "/protected/users/:uid/transports/:transport/:new_transport/test";
    
    public static final String POST_SECRET_URI = "/protected/users/:uid/methods/:method/secret";
    
    public static final String POST_VERIFY_URI = "/protected/users/:uid/:otp/:api_password?";
    
    public static final String DELETE_TRANSPORT_URI = "/protected/users/:uid/transports/:transport";
    
    private EsupOtpUriConstants() {
        
    }
    
}
