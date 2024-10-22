/*
 * Copyright 2024 NetKnights GmbH - lukas.matusiewicz@netknights.it
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.renater.shibboleth.esup.otp;

import javax.annotation.Nullable;

/**
 * Class for getting and printing the version of the plugin.
 */
public final class Version
{
    /**
     * IdP plugin version.
     */
    @Nullable
    private static final String VERSION = Version.class.getPackage().getImplementationVersion();

    /**
     * Constructor.
     */
    private Version()
    {
    }

    /**
     * Main entry point to program.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args)
    {
        System.out.println(VERSION);
    }

    /**
     * Get the version of the plugin.
     *
     * @return version of the plugin
     */
    @Nullable
    public static String getVersion()
    {
        return VERSION;
    }
}