#!/bin/bash
# Download Gradle wrapper files

GRADLE_VERSION="8.5"
WRAPPER_DIR="scripts/kotlin-template"

mkdir -p "$WRAPPER_DIR/gradle/wrapper"

# Download gradle-wrapper.jar
curl -L "https://raw.githubusercontent.com/gradle/gradle/v${GRADLE_VERSION}/gradle/wrapper/gradle-wrapper.jar" \
  -o "$WRAPPER_DIR/gradle/wrapper/gradle-wrapper.jar"

# Download gradle-wrapper.properties
curl -L "https://raw.githubusercontent.com/gradle/gradle/v${GRADLE_VERSION}/gradle/wrapper/gradle-wrapper.properties" \
  -o "$WRAPPER_DIR/gradle/wrapper/gradle-wrapper.properties"

# Create gradlew script
cat > "$WRAPPER_DIR/gradlew" << 'EOF'
#!/bin/sh
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
exec java -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
EOF

chmod +x "$WRAPPER_DIR/gradlew"

# Create gradlew.bat script
cat > "$WRAPPER_DIR/gradlew.bat" << 'EOF'
@echo off
set APP_HOME=%~dp0
java -jar "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*
EOF

echo "✓ Gradle wrapper downloaded to $WRAPPER_DIR"
