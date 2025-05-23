plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

val applicationId: String by rootProject.extra
val javaVersion: JavaVersion by rootProject.extra
val androidCompileSdk: Int by rootProject.extra
val androidMinSdk: Int by rootProject.extra
val appVersionName: String by rootProject.extra
val appVersionCode: Int by rootProject.extra

android {
    compileSdk = androidCompileSdk
    namespace = applicationId
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    defaultConfig {
        applicationId = applicationId
        minSdk = androidMinSdk
        targetSdk = androidCompileSdk
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        base.archivesName.set("CIFSDocumentsProvider-${versionName}")
    }

    buildTypes {
        debug {
            versionNameSuffix = "D"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    signingConfigs  {
        getByName("debug") {
            storeFile = file("$rootDir/debug.keystore")
        }
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion.majorVersion))
        }
    }

    packaging {
        // For commons-vfs2-jackrabbit2
        resources.excludes.addAll(setOf(
            "META-INF/*",
            "META-INF/versions/*/OSGI-INF/MANIFEST.MF",
        ))
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation(project(":common"))
    implementation(project(":presentation"))
    implementation(project(":domain"))

    implementation(libs.androidx.appcompat)
    implementation(libs.hilt.android)
    implementation(libs.androidx.work.runtime)
    ksp(libs.hilt.android.compiler)
}
