# Source this before running gradle:  source scripts/env.sh
# The machine-wide JAVA_HOME points to a removed JDK; pick a valid JDK 17 instead.
_ironlog_has_java() {
  [ -n "$1" ] && { [ -f "$1/bin/java.exe" ] || [ -f "$1/bin/java" ]; }
}
if ! _ironlog_has_java "${JAVA_HOME:-}"; then
  for c in "/c/Program Files/Microsoft/jdk-17.0.20.101-hotspot" "/c/Program Files/Microsoft/"jdk-17*; do
    if _ironlog_has_java "$c"; then
      export JAVA_HOME="$c"
      break
    fi
  done
fi
if [ -z "${ANDROID_HOME:-}" ]; then
  export ANDROID_HOME="$LOCALAPPDATA/Android/Sdk"
fi
echo "JAVA_HOME=$JAVA_HOME"
