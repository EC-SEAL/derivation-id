[![Build Status](https://travis-ci.org/EC-SEAL/derivation-id.svg?branch=development)](https://travis-ci.org/EC-SEAL/derivation-id)

# Derivation-id
Seal derivation ID Module

This module generates a dataSet with one attribute whose **name** is _http://project-seal.eu/2020/id/sealUUID_ and  **friendly name** is _sealUUID_.

The following steps need to be made in order to run the derivation ID microservice

#### 1. Environmental variables

The following environmental variables need to be set prior to running the project:

|Environment       | Description |
|------------------| ---- | 
| ASYNC_SYGNATURE  | Boolean value, if true denotes RSA signing for JWTs, else HS256 signing is conducted.|
| KEY_PASS         | Password for the certificate| 
| STORE_PASS       | Password for the keystore containing the certificate.| 
| HTTPSIG_CERT_ALIAS | Alias of the certificate used for the httpSig protocol. |
| SESSION_MANAGER_URL| Location of the Session Manager microservice. |
| CONFIGURATION_MANAGER_URL| Location of the Config Manager microservice. |
| KEY_STORE_PATH   |Path to the keystore holding the RSA certificate used for signing JWTs. 
| SENDER_ID | The name of this microservice ("uuid ms001" by default)
| SSL_KEYSTORE_PATH | Related to the ssl certificates
| SSL_STORE_PASS |
| SSL_KEY_PASS |
| SSL_CERT_ALIAS |

The following variables are related to the derived identity
| EXPIRATION_WINDOW | Expiration window in days (7 days by default).
| ISSUER_ID | "https://vm.project-seal.eu/" by default
| SUBJECT_ID | "sealUUID" by default
| DERIVED_DATASET_TYPE | "derivedID" by default
| ISSUER | "SEAL Automated Linker" by default
| LINK_REQUEST_TYPE | "linkedID" by default
| LOA | 4 by default
| DERIVED_ID_CATEGORIES | "UUID4" by default
| UUID_FRIENDLY_NAME | "sealUUID" by default
| UUID_ATTR_NAME | "http://project-seal.eu/2020/id/sealUUID" by default
| ATTRIBUTE_ENCODING | "plain" by default
| MANDATORY | true by default






#### 2. Run


After setting the environmental variables, just run: 

```mvn spring-bot:run ```


#### 2. Test
Runs unit tests 


```mvn spring-bot:test ```

