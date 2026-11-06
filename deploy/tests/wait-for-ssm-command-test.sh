#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
TEST_DIR="$(mktemp -d)"
trap 'rm -rf "$TEST_DIR"' EXIT

cat > "$TEST_DIR/aws" <<'EOF'
#!/usr/bin/env bash
set -Eeuo pipefail

COUNT_FILE="$FAKE_AWS_COUNT_FILE"
COUNT=0
if [[ -f "$COUNT_FILE" ]]; then
  COUNT="$(cat "$COUNT_FILE")"
fi

if [[ " $* " == *" --query Status "* ]]; then
  COUNT=$((COUNT + 1))
  printf '%s' "$COUNT" > "$COUNT_FILE"
  if (( COUNT <= 21 )); then
    printf 'InProgress\n'
  else
    printf 'Success\n'
  fi
  exit 0
fi

printf '{"Status":"Success","ResponseCode":0}\n'
EOF
chmod +x "$TEST_DIR/aws"

export FAKE_AWS_COUNT_FILE="$TEST_DIR/aws-count"
export PATH="$TEST_DIR:$PATH"
export SSM_POLL_INTERVAL_SECONDS=0
export SSM_MAX_ATTEMPTS=25

OUTPUT="$(bash "$ROOT_DIR/deploy/scripts/wait-for-ssm-command.sh" command-id instance-id)"

grep -Fq 'SSM status: Success (22/25)' <<< "$OUTPUT"
grep -Fq '"Status":"Success"' <<< "$OUTPUT"

printf 'PASS: waits beyond the AWS default waiter limit until success\n'
