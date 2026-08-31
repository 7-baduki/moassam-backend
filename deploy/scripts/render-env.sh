#!/usr/bin/env bash
set -Eeuo pipefail

ENVIRONMENT="${1:?environment is required: dev or prod}"
AWS_REGION="${AWS_REGION:-ap-northeast-2}"
DEPLOY_DIR="${DEPLOY_DIR:-/opt/moassam/current/deploy}"
TARGET_ENV_FILE="${DEPLOY_DIR}/.env"

if [[ ! "$ENVIRONMENT" =~ ^(dev|prod)$ ]]; then
  echo "Unsupported environment: $ENVIRONMENT" >&2
  exit 1
fi

if [[ ! -d "$DEPLOY_DIR" ]]; then
  echo "Deploy directory does not exist: $DEPLOY_DIR" >&2
  exit 1
fi

umask 077
TEMP_ENV_FILE="$(mktemp "${DEPLOY_DIR}/.env.XXXXXX")"
trap 'rm -f "$TEMP_ENV_FILE"' EXIT

aws ssm get-parameter \
  --name "/moassam/${ENVIRONMENT}/env" \
  --with-decryption \
  --region "$AWS_REGION" \
  --query 'Parameter.Value' \
  --output text > "$TEMP_ENV_FILE"

if [[ ! -s "$TEMP_ENV_FILE" ]]; then
  echo "Parameter Store returned an empty environment file" >&2
  exit 1
fi

for REQUIRED_KEY in \
  SPRING_PROFILES_ACTIVE \
  DB_NAME \
  DB_USER \
  DB_PASSWORD \
  JWT_SECRET \
  S3_BUCKET \
  STORAGE_PUBLIC_BASE_URL \
  CLOUDFRONT_DISTRIBUTION_ID \
  NGINX_SERVER_NAMES \
  NGINX_CERT_NAME
do
  if ! grep -q "^${REQUIRED_KEY}=" "$TEMP_ENV_FILE"; then
    echo "Missing required parameter: ${REQUIRED_KEY}" >&2
    exit 1
  fi
done

chmod 600 "$TEMP_ENV_FILE"
mv -f "$TEMP_ENV_FILE" "$TARGET_ENV_FILE"
trap - EXIT
