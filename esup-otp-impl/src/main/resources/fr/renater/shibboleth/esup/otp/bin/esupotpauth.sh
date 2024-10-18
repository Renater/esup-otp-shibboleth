#!/usr/bin/env bash

declare LOCATION

LOCATION=$(dirname $0)

$LOCATION/runclass.sh fr.renater.shibboleth.esup.otp.impl.EsupOtpAuthenticatorCLI classpath:/META-INF/fr/renater/shibboleth/esup/flows/authn/otp/EsupOtp-authenticator.xml "$@"