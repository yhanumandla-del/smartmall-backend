#!/bin/sh
set -e
MAVEN_VERSION=3.9.11
BASE="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_HOME="$BASE/.maven/apache-maven-$MAVEN_VERSION"
if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  ZIP="${TMPDIR:-/tmp}/apache-maven-$MAVEN_VERSION-bin.zip"
  URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$MAVEN_VERSION/apache-maven-$MAVEN_VERSION-bin.zip"
  echo "Maven is not installed. Downloading Apache Maven $MAVEN_VERSION..."
  mkdir -p "$BASE/.maven"
  if command -v curl >/dev/null 2>&1; then curl -fL "$URL" -o "$ZIP"; else wget -q "$URL" -O "$ZIP"; fi
  python3 -c "import zipfile; zipfile.ZipFile('$ZIP').extractall('$BASE/.maven')"
  rm -f "$ZIP"
fi
exec "$MAVEN_HOME/bin/mvn" "$@"
