#!/usr/bin/env bash
set -Eeuo pipefail

ENVIRONMENT="${1:?environment is required: dev or prod}"
IMAGE_NAME="${2:?image name is required}"
AWS_REGION="${AWS_REGION:-ap-northeast-2}"
DEPLOY_DIR="/opt/moassam/current/deploy"

"${DEPLOY_DIR}/scripts/render-env.sh" "$ENVIRONMENT"
printf '\nIMAGE_NAME=%s\n' "$IMAGE_NAME" >> "${DEPLOY_DIR}/.env"

GHCR_USERNAME="$(aws ssm get-parameter \
  --name "/moassam/${ENVIRONMENT}/GHCR_USERNAME" \
  --with-decryption \
  --region "$AWS_REGION" \
  --query 'Parameter.Value' \
  --output text)"

GHCR_TOKEN="$(aws ssm get-parameter \
  --name "/moassam/${ENVIRONMENT}/GHCR_TOKEN" \
  --with-decryption \
  --region "$AWS_REGION" \
  --query 'Parameter.Value' \
  --output text)"

trap 'unset GHCR_TOKEN' EXIT
printf '%s' "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USERNAME" --password-stdin

chmod +x "${DEPLOY_DIR}/scripts/"*.sh
bash "${DEPLOY_DIR}/scripts/deploy.sh"
