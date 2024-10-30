#!/usr/bin/env bash

declare LOCATION

LOCATION=$(dirname $0)

$LOCATION/runclass.sh fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl.EsupOtpAuthenticatorCLI classpath:/META-INF/net/shibboleth/idp/flows/authn/EsupOtp/EsupOtp-authenticator.xml "$@"