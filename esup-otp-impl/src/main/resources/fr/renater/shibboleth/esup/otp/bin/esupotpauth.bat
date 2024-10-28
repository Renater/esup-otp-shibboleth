@echo off
setlocal

"%~dp0\runclass.bat" fr.renater.shibboleth.esup.otp.impl.EsupOtpAuthenticatorCLI classpath:/META-INF/net/shibboleth/idp/flows/authn/EsupOtp/EsupOtp-authenticator.xml %*
