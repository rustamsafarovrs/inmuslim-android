import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "tj.rsdevteam.inmuslim"
    compileSdk = libs.versions.androidSdk.get().toInt()

    defaultConfig {
        applicationId = "tj.rsdevteam.inmuslim"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidSdk.get().toInt()
        versionCode = 7
        versionName = "1.2.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
        manifestPlaceholders["appName"] = "@string/app_name"
    }
    signingConfigs {
        getByName("debug") {
            storeFile = file("$rootDir/config/debug.keystore")
            storePassword = project.properties["DEBUG_STORE_PASSWORD"] as String
            keyAlias = project.properties["DEBUG_KEY_ALIAS"] as String
            keyPassword = project.properties["DEBUG_KEY_PASSWORD"] as String
        }
        create("release") {
            val localPropertiesFile = File(rootProject.rootDir, "local.properties")
            if (localPropertiesFile.exists()) {
                val properties = Properties().apply {
                    load(FileInputStream(localPropertiesFile))
                }
                storeFile = file("$rootDir/config/release.keystore")
                storePassword = properties.getProperty("RELEASE_STORE_PASSWORD", "")
                keyAlias = properties.getProperty("RELEASE_KEY_ALIAS", "")
                keyPassword = properties.getProperty("RELEASE_KEY_PASSWORD", "")
            }
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".beta"
            signingConfig = signingConfigs.getByName("debug")
            manifestPlaceholders["appName"] = "Inmuslim Beta"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/okhttp3/internal/publicsuffix/NOTICE"
            excludes += "DebugProbesKt.bin"
            excludes += "/META-INF/com/android/build/gradle/app-**.properties"
        }
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(libs.versions.java.get()))
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.junit4)

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
    ksp(libs.dagger.hilt.android.compiler)
    implementation(libs.dagger.hilt.navigation)

    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.retrofit.adapters.result)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(libs.google.play.review.ktx)
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":res"))
    implementation(project(":uicomponents"))
    implementation(project(":feature:tasbih"))
}
