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
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Class to manager if request is retryable or not.
 */
public class RetryOverHttpError implements HttpRequestRetryStrategy {

    /** Class logger. */
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(RetryOverHttpError.class);

    @Override
    public boolean retryRequest(
            final HttpRequest request, final IOException exception, final int execCount, final HttpContext context) {
        // Do not retry if over max retry count
        return execCount <= 5;
    }

    @Override
    public boolean retryRequest(final HttpResponse response, final int execCount, final HttpContext context) {
        // Do not retry if over max retry count
        if (execCount > 5) {
            return false;
        }

        try {
            final int responseCode = response.getCode();

            if (responseCode >= 500 && responseCode <= 599) {
                log.debug("Retrying for 5xx code");
                return true;
            } else if (responseCode == 429) {
                log.debug("Too many requests, retrying...");
                return true;
            }

        } catch (final Exception e) {
            log.debug("Caught Exception");
        }
        return false;
    }

    @Override
    public TimeValue getRetryInterval(final HttpResponse response, final int execCount, final HttpContext context) {
        return TimeValue.of(5000, TimeUnit.MILLISECONDS);
    }
}
