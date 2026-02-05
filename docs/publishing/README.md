# Publishing Guide

## What Changed

### Modernized Gradle Scripts

1. **Removed IS_NEXT** - Simplified to only Android and Netty builds
2. **Removed jcenter()** - Deprecated and shut down, now using only Google and Maven Central
3. **Updated to Gradle 8.12** - Latest stable version
4. **Used tasks.register** - Lazy task configuration for better build performance
5. **Updated dependencies** to latest versions:
   - Jackson: 2.16.1 → 2.18.2
   - Web3j: 4.11.0 → 4.12.3
   - Netty: 4.1.106 → 4.1.115
   - Mockito: 5.10.0 → 5.14.2
   - Android compileSdk: 34 → 35
   - Android Gradle Plugin: 8.2.2 → 8.7.3
6. **Removed Sonatype/Maven Central publishing** - Only publishes to local Maven (since this is a fork)
7. **Removed signing requirement** - Not needed for local or JitPack publishing
8. **Fixed deprecated Android DSL** - Updated `lintOptions` → `lint`, `packagingOptions` → `packaging`

## How to Use This Fork

### Option 1: JitPack (Recommended - No Build Required)

Add to your project's `build.gradle`:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.YOUR_GITHUB_USERNAME:autobahn-java:TAG_OR_BRANCH'
}
```

Examples:
- `'com.github.YOUR_GITHUB_USERNAME:autobahn-java:v21.4.2'` - Specific tag
- `'com.github.YOUR_GITHUB_USERNAME:autobahn-java:feature/add-comprehensive-tests-SNAPSHOT'` - Specific branch

### Option 2: Local Maven (For Local Development)

Build and publish to your local Maven cache:

```bash
# For Android
./gradlew publishToMavenLocal -PbuildPlatform=android

# For Netty/Java
./gradlew publishToMavenLocal -PbuildPlatform=netty
```

Then in your project:

```gradle
repositories {
    mavenLocal()
}

dependencies {
    implementation 'io.crossbar.autobahn:autobahn-android:21.4.2-SNAPSHOT'
}
```

### Option 3: GitHub Packages (Optional)

If you want to share with other private projects, you can add GitHub Packages. Add this to the `repositories` block in `autobahn/build.gradle`:

```gradle
maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/YOUR_USERNAME/autobahn-java")
    credentials {
        username = System.getenv("GITHUB_ACTOR")
        password = System.getenv("GITHUB_TOKEN")
    }
}
```

Then publish:
```bash
./gradlew publish -PbuildPlatform=android
```

## What is Sonatype/Maven Central?

**You don't need this for your fork!**

Sonatype runs Maven Central - the official public repository where libraries like Jackson, Mockito, etc. are published. Publishing there requires:
- Sonatype account registration
- Domain/package name verification
- GPG signing keys
- Complex approval process

Since you're using this fork privately (via JitPack or local Maven), you don't need any of that complexity.

## Current Version

- **Version**: 21.4.2-SNAPSHOT
- **Group ID**: io.crossbar.autobahn
- **Artifact ID**: autobahn-android (for Android) or autobahn-java (for Netty)

## Build Variants

- **Android**: `./gradlew build -PbuildPlatform=android`
- **Netty** (Java 17): `./gradlew build -PbuildPlatform=netty`
