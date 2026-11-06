#!/usr/bin/env bash
set -Eeuo pipefail

COMMAND_ID="${1:?command id is required}"
INSTANCE_ID="${2:?instance id is required}"
POLL_INTERVAL_SECONDS="${SSM_POLL_INTERVAL_SECONDS:-5}"
MAX_ATTEMPTS="${SSM_MAX_ATTEMPTS:-180}"

print_invocation() {
  aws ssm get-command-invocation \
    --command-id "$COMMAND_ID" \
    --instance-id "$INSTANCE_ID"
}

for ((attempt = 1; attempt <= MAX_ATTEMPTS; attempt++)); do
  set +e
  STATUS_OUTPUT="$(aws ssm get-command-invocation \
    --command-id "$COMMAND_ID" \
    --instance-id "$INSTANCE_ID" \
    --query 'Status' \
    --output text 2>&1)"
  STATUS_EXIT=$?
  set -e

  if (( STATUS_EXIT != 0 )); then
    if grep -q 'InvocationDoesNotExist' <<< "$STATUS_OUTPUT"; then
      STATUS="Pending"
    else
      printf '%s\n' "$STATUS_OUTPUT" >&2
      exit "$STATUS_EXIT"
    fi
  else
    STATUS="$STATUS_OUTPUT"
  fi

  printf 'SSM status: %s (%d/%d)\n' "$STATUS" "$attempt" "$MAX_ATTEMPTS"

  case "$STATUS" in
    Success)
      print_invocation
      exit 0
      ;;
    Failed|Cancelled|TimedOut)
      print_invocation || true
      exit 1
      ;;
    Pending|InProgress|Delayed|Cancelling)
      ;;
    *)
      printf 'Unknown SSM command status: %s\n' "$STATUS" >&2
      print_invocation || true
      exit 1
      ;;
  esac

  if (( attempt < MAX_ATTEMPTS )); then
    sleep "$POLL_INTERVAL_SECONDS"
  fi
done

printf 'SSM command did not finish after %d attempts.\n' "$MAX_ATTEMPTS" >&2
print_invocation || true
exit 124
