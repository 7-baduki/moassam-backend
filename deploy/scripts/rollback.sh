#!/bin/bash
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

REGISTRY="ghcr.io/7-baduki/moassam-backend"
DEPLOY_DIR="/root/moassam/deploy"
HEALTH_CHECK_URL="http://localhost:8080/actuator/health"
MAX_RETRY=6
RETRY_INTERVAL=10

echo -e "${YELLOW}[1/5] 롤백 이미지 확인${NC}"
PREVIOUS_IMAGE=$(docker images ${REGISTRY}:previous -q || true)
if [ -z "${PREVIOUS_IMAGE:-}" ]; then
  echo -e "${RED}[ERROR] 롤백할 이미지 없음${NC}"
  exit 1
fi

echo -e "${YELLOW}[2/5] 현재 앱 중지${NC}"
cd "$DEPLOY_DIR"
docker compose -f docker-compose.app.yaml down 2>/dev/null || docker rm -f moassam-api 2>/dev/null || true

echo -e "${YELLOW}[3/5] 이전 버전으로 태그 변경${NC}"
docker tag ${REGISTRY}:previous ${REGISTRY}:latest

echo -e "${YELLOW}[4/5] 이전 버전 시작${NC}"
docker compose -f docker-compose.app.yaml up -d
sleep 10

echo -e "${YELLOW}[5/5] 헬스체크${NC}"
RETRY_COUNT=0
while [ $RETRY_COUNT -lt $MAX_RETRY ]; do
  if curl -f -s --max-time 5 "$HEALTH_CHECK_URL" > /dev/null 2>&1; then
    echo -e "${GREEN}롤백 성공${NC}"
    exit 0
  fi
  RETRY_COUNT=$((RETRY_COUNT + 1))
  sleep $RETRY_INTERVAL
done

echo -e "${RED}롤백 실패 - 수동 복구 필요${NC}"
exit 1