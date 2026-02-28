#!/usr/bin/env bash

echo "Running detekt check..."
DETEKT_OUTPUT="/tmp/detekt-$(date +%s)"
./gradlew detekt > "$DETEKT_OUTPUT"
DETEKT_EXIT=$?

if [ $DETEKT_EXIT -ne 0 ]; then
  cat "$DETEKT_OUTPUT"
  rm "$DETEKT_OUTPUT"
  echo "***********************************************"
  echo "                 detekt failed                 "
  echo " Please fix the above issues before committing "
  echo "***********************************************"
  exit $DETEKT_EXIT
fi
rm "$DETEKT_OUTPUT"

echo "Running Android Lint check..."
LINT_OUTPUT="/tmp/lint-$(date +%s)"
./gradlew lint > "$LINT_OUTPUT"
LINT_EXIT=$?

if [ $LINT_EXIT -ne 0 ]; then
  cat "$LINT_OUTPUT"
  rm "$LINT_OUTPUT"
  echo "***********************************************"
  echo "                  lint failed                  "
  echo " Please fix the above issues before committing "
  echo "***********************************************"
  exit $LINT_EXIT
fi
rm "$LINT_OUTPUT"
