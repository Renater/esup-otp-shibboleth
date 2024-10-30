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

package fr.renater.shibboleth.idp.plugin.authn.esup.otp;

import java.io.IOException;

import net.shibboleth.idp.module.IdPModule;
import net.shibboleth.idp.plugin.impl.FirstPartyIdPPlugin;
import net.shibboleth.profile.module.ModuleException;
import net.shibboleth.profile.plugin.PluginException;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Details about the Esup otp login plugin.
 */
public class EsupOtpPlugin extends FirstPartyIdPPlugin {

    /**
     * Constructor.
     *
     * @throws IOException
     * @throws PluginException
     */
    public EsupOtpPlugin() throws IOException, PluginException {
        super(EsupOtpPlugin.class);
        try {
            final IdPModule module = new EsupOtpModule();
            setEnableOnInstall(CollectionSupport.singleton(module));
            setDisableOnRemoval(CollectionSupport.singleton(module));
        } catch (final IOException e) {
            throw e;
        } catch (final ModuleException e) {
            throw new PluginException(e);
        }
    }

}
