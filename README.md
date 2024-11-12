<!-- TOC -->
* [Esup Otp Shibboleth](#esup-otp-shibboleth)
  * [Description](#description)
  * [Installation](#installation)
    * [Copy package to the server](#copy-package-to-the-server)
    * [Enable Multifactor Module](#enable-multifactor-module)
    * [Install the plugin](#install-the-plugin)
  * [Configuration](#configuration)
  * [Log check](#log-check)
  * [Test](#test)
<!-- TOC -->

# Esup Otp Shibboleth

## Description

Esup Otp Shibboleth est un plugin Shibboleth.

Ce plugin est à utilisé au sein d'un login flow de type Multi-Factor. Il permet d'appeler l'api REST esup-otp


| Plugin ID                      | Module(s)         | Authentication Flow ID |
|--------------------------------|-------------------|------------------------|
| fr.renater.shibboleth.esup.otp | idp.authn.esupotp | authn/EsupOtp          |

## Installation

### Copy package to the server

```
$ scp module.tar.gz username@server:/tmp/.
$ scp module.tar.gz.asc username@server:/tmp/.
$ ssh username@server -i ~/.ssh/id_rsa
[username@server ~]$ sudo -i
[username@server ~]$ export idp_install_path=/usr/share/shibboleth-idp
[username@server ~]$ cp /tmp/module.tar.gz $idp_install_path/plugins/.
[username@server ~]$ cp /tmp/module.tar.gz.asc $idp_install_path/plugins/.
```

### Enable Multifactor Module

From shibboleth documentation [MultiFactorAuthnConfiguration](https://shibboleth.atlassian.net/wiki/spaces/IDP5/pages/3199505534/MultiFactorAuthnConfiguration)

```
[username@server ~]$ $idp_install_path/bin/module.sh -t idp.authn.MFA || $idp_install_path/bin/module.sh -e idp.authn.MFA
```

Check

```
[username@server ~]$ $idp_install_path/bin/module.sh -l
```

### Install the plugin

```
[username@server ~]$ $idp_install_path/bin/plugin.sh -i $idp_install_path/plugins/module.tar.gz --noCheck
[username@server ~]$ systemctl restart tomcat10.service
```

#### Tomcat installation not standard
If your tomcat configuration does not point to the war regenerated after plugin installation, such as the following configuration for example:
```
[username@server ~]$ cat /etc/tomcat10/Catalina/localhost/idp.xml
<Context
    docBase="$idp_install_path/war/idp.war"
    privileged="true"
    swallowOutput="true">
```

You need to copy jar files into docBase directory to plugin work well:

1. Copy jar into WEB-INF/lib
2. Add permission on jar files
3. restart service

```
[username@server ~]$ cp $idp_install_path/dist/plugin-webapp/WEB-INF/lib/esup-otp-* $idp_install_path/webapp/WEB-INF/lib/.
[username@server ~]$ chmod o+r $idp_install_path/webapp/WEB-INF/lib/esup-otp-*
```

- Remove plugin
```
[username@server ~]$ $idp_install_path/bin/plugin.sh -r fr.renater.shibboleth.esup.otp
```

## Configuration

| property                     |                      required                      | Default value    | Description |
|:-----------------------------|:--------------------------------------------------:|------------------|-------------|
| idp.esup.otp.apiHost         |                      &check;                       |                  |             |
| idp.duo.oidc.apiPassword     |                      &check;                       |                  |             |
| idp.esup.oidc.redirectURL    |                                                    |                  |             |
| idp.esup.otp.endpoint.health |                                                    | /v1/health_check |             |
|                              |                                                    |                  |             |


## Log configuration

To activate debug logs for plugin you need to edit conf/logback.xml file like this : 

```
<!-- To log request and response from esup-otp-api -->
<logger name="org.apache.hc.client5.http.wire" level="DEBUG" />
<!-- To add debug logs of plugin -->
<logger name="fr.renater.shibboleth.idp.plugin.authn.esup.otp.impl" level="DEBUG" />
```

## Log check

- Main Log : ```$idp_install_path/logs/idp-process.log```
- Warn and error log : ```$idp_install_path/logs/idp-warn.log```

## Test

- Command utils
```
[username@server ~]$ $idp_install_path/bin/esupotpauth.sh --help
```

- List all user uids

```
[username@server ~]$ $idp_install_path/bin/esupotpauth.sh --home /usr/share/shibboleth-idp --verbose --command all
```

### Development

Java 17, Spring framework 6, Lombok