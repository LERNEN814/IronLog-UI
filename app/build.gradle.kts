import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
}

android {
    namespace = "com.ironlog.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.ironlog.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            val stageKeystore = rootProject.file(".stage-signing/ironlog-phase1.jks")
            if (stageKeystore.exists()) {
                signingConfig = signingConfigs.create("stage") {
                    storeFile = stageKeystore
                    storePassword = System.getenv("IRONLOG_STAGE_STORE_PASSWORD")
                    keyAlias = System.getenv("IRONLOG_STAGE_KEY_ALIAS")
                    keyPassword = System.getenv("IRONLOG_STAGE_KEY_PASSWORD")
                }
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.all {
            // Robolectric's native runtime (used for SDK 35+ tests) locates the
            // android-all jar via a URL-encoded path and breaks when the path
            // contains spaces (Windows). Point its Maven cache at a space-free
            // directory whenever the repo path itself contains a space.
            val inRepoCache = rootProject.file(".robolectric-m2")
            val cacheDir = if (System.getProperty("os.name").startsWith("Windows") &&
                inRepoCache.absolutePath.contains(' ')
            ) {
                File(System.getenv("PUBLIC") ?: "C:\\Users\\Public", "robolectric-m2")
            } else {
                inRepoCache
            }
            it.systemProperty("maven.repo.local", cacheDir.absolutePath)
        }
    }
    sourceSets {
        // Robolectric reads merged debug assets; expose Room schemas for MigrationTestHelper (debug only).
        getByName("debug").assets.srcDir("$projectDir/schemas")
    }
    lint {
        abortOnError = true
        warningsAsErrors = false
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.navigation.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.lifecycle.viewmodel.compose)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.room.testing)
}
