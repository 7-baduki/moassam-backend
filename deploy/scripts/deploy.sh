#!/bin/bash
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

WORK_DIR="/home/abcd010531/moassam"
DEPLOY_DIR="$WORK_DIR/deploy"
ENV_FILE="$DEPLOY_DIR/.env"
HEALTH_CHECK_URL="http://localhost:8080/actuator/health"
MAX_RETRY=30
RETRY_INTERVAL=10

get_env_value() {
  local key="$1"

  if [ -f "$ENV_FILE" ]; then
    grep -E "^${key}=" "$ENV_FILE" | tail -n 1 | cut -d '=' -f 2-
  fi
}

IMAGE_NAME="${IMAGE_NAME:-$(get_env_value IMAGE_NAME)}"
IMAGE_NAME="${IMAGE_NAME:-ghcr.io/7-baduki/moassam-backend:latest}"

PREVIOUS_IMAGE_NAME="${PREVIOUS_IMAGE_NAME:-$(get_env_value PREVIOUS_IMAGE_NAME)}"
PREVIOUS_IMAGE_NAME="${PREVIOUS_IMAGE_NAME:-ghcr.io/7-baduki/moassam-backend:previous}"


cd "$WORK_DIR" || exit 1

echo -e "${YELLOW}[1/8] 현재 이미지 롤백용 보관${NC}"
CURRENT_IMAGE=$(docker images "$IMAGE_NAME" -q || true)
if [ ! -z "${CURRENT_IMAGE:-}" ]; then
  docker rmi "$PREVIOUS_IMAGE_NAME" 2>/dev/null || true
  docker tag "$IMAGE_NAME" "$PREVIOUS_IMAGE_NAME"
  echo -e "${GREEN}[SUCCESS] 롤백용 이미지 준비 완료 $PREVIOUS_IMAGE_NAME${NC}"
else
  echo -e "${YELLOW}[INFO] 기존 이미지 없음 (첫 배포)${NC}"
fi

echo -e "${YELLOW}[2/8] 오래된 이미지 정리${NC}"
docker image prune -f || true

echo -e "${YELLOW}[3/8] 최신 이미지 다운로드 $IMAGE_NAME${NC}"
docker pull "$IMAGE_NAME"
NEW_IMAGE=$(docker images "$IMAGE_NAME" -q || true)
echo -e "${GREEN}[SUCCESS] 새 이미지: ${NEW_IMAGE}${NC}"

cd "$DEPLOY_DIR" || exit 1

echo -e "${YELLOW}[4/8] DB 컨테이너 확인${NC}"
if ! docker ps --format '{{.Names}}' | grep -q '^moassam-db$'; then
  docker compose -f docker-compose.db.yaml up -d
  echo -e "${GREEN}[SUCCESS] DB 시작${NC}"
  sleep 5
else
  echo -e "${GREEN}[SUCCESS] DB 이미 실행 중${NC}"
fi

set -a
source "$DEPLOY_DIR/.env"
set +a

: "${NGINX_SERVER_NAMES:?NGINX_SERVER_NAMES is required}"
: "${NGINX_CERT_NAME:?NGINX_CERT_NAME is required}"

envsubst '${NGINX_SERVER_NAMES} ${NGINX_CERT_NAME}' \
  < "$DEPLOY_DIR/nginx.conf" \
  > "$DEPLOY_DIR/nginx.rendered.conf"

echo -e "${YELLOW}[5/8] Nginx 컨테이너 시작/갱신${NC}"
docker compose -f docker-compose.nginx.yaml up -d --force-recreate
echo -e "${GREEN}[SUCCESS] Nginx 시작/갱신${NC}"

echo -e "${YELLOW}[6/8] 기존 앱 컨테이너 중지${NC}"
docker compose -f docker-compose.app.yaml down 2>/dev/null || docker rm -f moassam-api 2>/dev/null || true

echo -e "${YELLOW}[7/8] 새 앱 컨테이너 시작${NC}"
docker compose -f docker-compose.app.yaml up -d
if [ $? -ne 0 ]; then
  echo -e "${RED}[ERROR] 시작 실패 - 롤백${NC}"
  bash "$DEPLOY_DIR/scripts/rollback.sh"
  exit 1
fi

sleep 10

echo -e "${YELLOW}[8/8] 헬스체크${NC}"
RETRY_COUNT=0
HEALTH_OK=false
while [ $RETRY_COUNT -lt $MAX_RETRY ]; do
  if curl -f -s --max-time 5 "$HEALTH_CHECK_URL" > /dev/null 2>&1; then
    HEALTH_OK=true
    break
  else
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo -e "${YELLOW}[RETRY] (${RETRY_COUNT}/${MAX_RETRY})${NC}"
    [ $RETRY_COUNT -eq 3 ] && docker logs --tail 30 moassam-api || true
    sleep $RETRY_INTERVAL
  fi
done

if [ "$HEALTH_OK" = false ]; then
  echo -e "${RED}[ERROR] 헬스체크 실패 - 롤백${NC}"
  docker logs --tail 100 moassam-api || true
  bash "$DEPLOY_DIR/scripts/rollback.sh"
  exit 1
fi

docker image prune -f || true

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  배포 완료!${NC}"
echo -e "${GREEN}========================================${NC}"