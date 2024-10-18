@echo off
setlocal

"%~dp0\runclass.bat" fr.renater.shibboleth.esup.otp.impl.EsupOtpAuthenticatorCLI classpath:/META-INF/fr/renater/shibboleth/esup/flows/authn/otp/EsupOtp-authenticator.xml %*
