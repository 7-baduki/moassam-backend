#!/bin/bash
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

REGISTRY="ghcr.io/7-baduki/moassam-backend"
WORK_DIR="/root/moassam"
DEPLOY_DIR="$WORK_DIR/deploy"
HEALTH_CHECK_URL="http://localhost:8080/actuator/health"
MAX_RETRY=6
RETRY_INTERVAL=10

cd "$WORK_DIR" || exit 1

echo -e "${YELLOW}[1/8] 현재 이미지 롤백용 보관${NC}"
CURRENT_IMAGE=$(docker images ${REGISTRY}:latest -q || true)
if [ ! -z "${CURRENT_IMAGE:-}" ]; then
  docker rmi ${REGISTRY}:previous 2>/dev/null || true
  docker tag ${REGISTRY}:latest ${REGISTRY}:previous
  echo -e "${GREEN}[SUCCESS] 롤백용 이미지 준비 완료${NC}"
else
  echo -e "${YELLOW}[INFO] 기존 이미지 없음 (첫 배포)${NC}"
fi

echo -e "${YELLOW}[2/8] 오래된 이미지 정리${NC}"
OLD_IMAGES=$(docker images ${REGISTRY} -q | tail -n +3 || true)
if [ ! -z "${OLD_IMAGES:-}" ]; then
  echo "$OLD_IMAGES" | xargs docker rmi -f 2>/dev/null || true
fi

echo -e "${YELLOW}[3/8] 최신 이미지 다운로드${NC}"
docker pull ${REGISTRY}:latest
NEW_IMAGE=$(docker images ${REGISTRY}:latest -q || true)
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

echo -e "${YELLOW}[5/8] Nginx 컨테이너 확인${NC}"
if ! docker ps --format '{{.Names}}' | grep -q '^moassam-nginx$'; then
  docker compose -f docker-compose.nginx.yaml up -d
  echo -e "${GREEN}[SUCCESS] Nginx 시작${NC}"
else
  echo -e "${GREEN}[SUCCESS] Nginx 이미 실행 중${NC}"
fi

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