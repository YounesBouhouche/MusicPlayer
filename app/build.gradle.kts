import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
}

android {
    namespace = "younesbouhouche.musicplayer"
    compileSdk = 35
    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "younesbouhouche.musicplayer"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        javaCompileOptions {
            annotationProcessorOptions {
                argument("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", "\"https://api.deezer.com/\"")
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"https://api.deezer.com/\"")
        }
        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            isDebuggable = false
            proguardFiles("benchmark-rules.pro")
        }
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget("21")
            languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion("2.2")
            apiVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.fromVersion("2.2")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

//tasks.getByPath("preBuild").dependsOn("ktlintFormat")

dependencies {
    implementation(libs.material.kolor)
    implementation(libs.kmpalette.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.sh.reorderable)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    //implementation(libs.material)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.androidx.material3.adaptive.navigation.suite.android)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.ui)
    implementation(libs.material.motion.compose.navigation)
    implementation(libs.waveslider)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.jaudiotagger)
    implementation(libs.timber)
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.glance.material)
    implementation(libs.converter.gson)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.datastore.preferences)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    ksp(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.gson)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.coil.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    implementation(libs.androidx.collection)
    implementation(libs.androidx.collection.ktx)
    implementation(libs.disk.lru.cache)
    implementation(libs.bundles.ktor)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.androidx.animation.graphics)
}

