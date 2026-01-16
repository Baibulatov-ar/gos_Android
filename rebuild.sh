#!/bin/bash

echo "Cleaning and rebuilding project..."

# Remove problematic transform cache - try multiple times if needed
for i in {1..3}; do
    rm -rf ~/.gradle/caches/transforms-3/2c647a2fb7507791973bbf698cb9b6bf 2>/dev/null
    rm -rf ~/.gradle/caches/transforms-3/421c820194233668bebb281f417a909b 2>/dev/null
    sleep 1
done

# Clean project
cd "$(dirname "$0")"
./gradlew clean --no-daemon

echo "Building..."
./gradlew assembleDebug --no-daemon

if [ $? -eq 0 ]; then
    echo "✅ Build succeeded! Try running the app now."
else
    echo "❌ Build failed. Try:"
    echo "   1. In Android Studio: File → Invalidate Caches / Restart"
    echo "   2. Run: rm -rf ~/.gradle/caches/transforms-3"
    echo "   3. Rebuild project"
fi
