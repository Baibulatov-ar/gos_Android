// Top-level build file
plugins {
    id("com.android.application") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

// Create 'test' alias for convenience - runs only debug unit tests
tasks.register("test") {
    dependsOn(":app:testDebugUnitTest")
    group = "verification"
    description = "Runs unit tests (debug variant only)"
}

