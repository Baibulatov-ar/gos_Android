#!/bin/bash

echo "Fixing jlink issue..."

# Remove problematic transform cache
rm -rf ~/.gradle/caches/transforms-3/2c647a2fb7507791973bbf698cb9b6bf
rm -rf ~/.gradle/caches/transforms-3/421c820194233668bebb281f417a909b

# Clean project build
cd "$(dirname "$0")"
./gradlew clean --no-daemon

echo "Cache cleared. Now try building again."

