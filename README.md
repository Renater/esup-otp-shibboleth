# Esup Otp Shibboleth

## Description

Esup Otp Shibboleth est un plugin Shibboleth.

Ce plugin est à utilisé au sein d'un login flow de type Multi-Factor. Il permet d'appeler l'api REST esup-otp


| Plugin ID                      | Module(s)         | Authentication Flow ID |
|--------------------------------|-------------------|------------------------|
| fr.renater.shibboleth.esup.otp | idp.authn.esupotp | authn/esupotp          |

## Installation

```
C:>\opt\shibboleth-idp\bin\plugin.bat -I fr.renater.shibboleth.esup.otp
```

ou

```
$ /opt/shibboleth-idp/bin/plugin.sh -i <plugin.tar.gz>
```