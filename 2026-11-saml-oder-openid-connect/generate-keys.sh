#!/usr/bin/env bash
#
# Creates the key pair with which the application signs its SAML messages:
#   keys/sp-private.key, keys/sp-certificate.crt   read by the application
#   .env                                           certificate for Keycloak's realm import
# Neither belongs in Git (see .gitignore).
#
# Usage: ./generate-keys.sh            certificate valid for 365 days
#        CERT_DAYS=1 ./generate-keys.sh
#
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"
mkdir -p keys
openssl req -x509 -newkey rsa:3072 -nodes -days "${CERT_DAYS:-365}" \
    -subj "/CN=saml-vs-oidc-demo/O=rolfsblog.ch" \
    -keyout keys/sp-private.key -out keys/sp-certificate.crt 2>/dev/null
chmod 600 keys/sp-private.key
printf 'SAML_SP_CERTIFICATE=%s\n' "$(grep -v -- '-----' keys/sp-certificate.crt | tr -d '\n')" > .env

echo "Created keys/ and .env."
openssl x509 -enddate -noout -in keys/sp-certificate.crt
echo "A running Keycloak still knows the old certificate: docker compose up -d --force-recreate"
