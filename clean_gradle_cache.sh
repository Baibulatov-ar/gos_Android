#!/bin/bash

# Script to clean Gradle cache to fix jlink issues

echo "Cleaning Gradle transforms cache..."
rm -rf ~/.gradle/caches/transforms-3/2c647a2fb7507791973bbf698cb9b6bf
rm -rf ~/.gradle/caches/transforms-3/421c820194233668bebb281f417a909b

echo "Cleaning project build directory..."
rm -rf app/build

echo "Cache cleaned! Please rebuild the project in Android Studio."

