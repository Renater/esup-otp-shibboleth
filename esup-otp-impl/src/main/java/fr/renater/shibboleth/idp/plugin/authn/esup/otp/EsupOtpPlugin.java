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
