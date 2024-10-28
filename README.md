# Esup Otp Shibboleth

## Description

Esup Otp Shibboleth est un plugin Shibboleth.

Ce plugin est à utilisé au sein d'un login flow de type Multi-Factor. Il permet d'appeler l'api REST esup-otp


| Plugin ID                      | Module(s)         | Authentication Flow ID |
|--------------------------------|-------------------|------------------------|
| fr.renater.shibboleth.esup.otp | idp.authn.esupotp | authn/esupotp          |

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
```

- Remove plugin
```
[username@server ~]$ $idp_install_path/bin/plugin.sh -r fr.renater.shibboleth.esup.otp
```

## Configuration

| property                     | required    | Default value    | Description |
|:-----------------------------|-------------|------------------|-------------|
| idp.esup.otp.apiHost         | X           |                  |             |
| idp.duo.oidc.apiPassword     | X           |                  |             |
| idp.esup.oidc.redirectURL    |             |                  |             |
| idp.esup.otp.endpoint.health |             | /v1/health_check |             |


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