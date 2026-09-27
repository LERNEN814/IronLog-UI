#!/usr/bin/env bash
# Milestone gate. Usage:
#   bash scripts/verify.sh           static rule checks + assembleDebug + unit tests + lint
#   bash scripts/verify.sh --quick   static rule checks only
set -u
cd "$(dirname "$0")/.."
source scripts/env.sh >/dev/null

PY=python
command -v python >/dev/null 2>&1 || PY=python3

$PY scripts/check_rules.py
static_rc=$?

if [ "${1:-}" = "--quick" ]; then
  exit $static_rc
fi

echo
echo "== Gradle: assembleDebug testDebugUnitTest lintDebug =="
rm -rf app/build/test-results/testDebugUnitTest
./gradlew --console=plain assembleDebug testDebugUnitTest lintDebug > build-verify.log 2>&1
gradle_rc=$?
tail -n 5 build-verify.log

$PY scripts/check_rules.py --results-only
results_rc=$?

fails=0
[ $static_rc -ne 0 ] && fails=$((fails+1))
[ $gradle_rc -ne 0 ] && { echo "FAIL gradle build (see build-verify.log)"; fails=$((fails+1)); }
[ $results_rc -ne 0 ] && fails=$((fails+1))

echo
if [ $fails -eq 0 ]; then
  echo "VERIFY RESULT: 0 FAIL"
  exit 0
else
  echo "VERIFY RESULT: $fails FAIL GROUP(S)"
  exit 1
fi
