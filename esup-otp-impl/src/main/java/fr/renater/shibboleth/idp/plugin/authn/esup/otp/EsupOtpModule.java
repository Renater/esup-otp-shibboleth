package fr.renater.shibboleth.idp.plugin.authn.esup.otp;

import java.io.IOException;

import net.shibboleth.idp.module.IdPModule;
import net.shibboleth.idp.module.impl.PluginIdPModule;
import net.shibboleth.profile.module.ModuleException;

/**
 * {@link IdPModule} implementation.
 */
public final class EsupOtpModule extends PluginIdPModule {

    /**
     * Constructor.
     *
     * @throws IOException
     * @throws ModuleException
     */
    public EsupOtpModule() throws IOException, ModuleException {
        super(EsupOtpModule.class);
    }
}
