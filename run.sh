#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

mkdir -p build/classes

javac -cp kennelapplication/lib/sqlite-jdbc.jar \
  -d build/classes \
  $(find kennelapplication/src/main/java -name '*.java')

java -cp build/classes:kennelapplication/lib/sqlite-jdbc.jar Main
