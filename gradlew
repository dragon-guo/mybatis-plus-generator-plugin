#!/bin/sh
#
# Gradle wrapper startup script for POSIX systems
#

# Determine the Java command to use
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Resolve the location of the gradle-wrapper.jar
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
CLASSPATH="$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar"

# Execute Gradle
exec "$JAVACMD" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
