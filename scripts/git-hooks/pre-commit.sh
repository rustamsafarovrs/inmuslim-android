#!/usr/bin/env bash
set -u

# Emergency escape hatch: SKIP_HOOKS=1 git commit ...
if [ "${SKIP_HOOKS:-0}" != "0" ]; then
  echo "SKIP_HOOKS is set — skipping detekt and lint."
  exit 0
fi

cd "$(git rev-parse --show-toplevel)" || exit 1

OUTPUT="$(mktemp -t inmuslim-hook)"
trap 'rm -f "$OUTPUT"' EXIT

banner() {
  echo "***********************************************"
  printf '%s\n' "$1"
  echo " Please fix the above issues before committing "
  echo "***********************************************"
}

# run_check <human name> <gradle task...>
run_check() {
  local name="$1"
  shift

  echo "Running $name check..."
  ./gradlew "$@" > "$OUTPUT" 2>&1
  local code=$?

  if [ $code -ne 0 ]; then
    cat "$OUTPUT"
    banner "                 $name failed"
    exit $code
  fi
}

run_check "detekt" detekt
run_check "lint" lint

exit 0
