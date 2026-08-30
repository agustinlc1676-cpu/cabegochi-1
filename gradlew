#!/usr/bin/env sh
DIRNAME=$(cd "$(dirname "$0")" && pwd)
DEFAULT_JVM_OPTS="-Xmx64m"
java -classpath "$DIRNAME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"

