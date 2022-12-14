plugins {
    id("com.android.application")

    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.7.20"
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 33
    namespace = "net.theluckycoder.qr"

    defaultConfig {
        applicationId = "net.theluckycoder.qr"
        minSdk = 30
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
        resourceConfigurations += listOf("en")

        /*javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }*/
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
//            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
        }
    }

    buildFeatures.compose = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).configureEach {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
    }
}

dependencies {
    val voyagerVersion = "1.0.0-rc02"

    // Kotlin
    kotlin("kotlin-stdlib-jdk8")
    debugImplementation(libs.kotlinReflect)
    implementation(libs.kotlinCoroutinesAndroid)
    implementation(libs.kotlinSerializationJson)
    implementation(libs.kotlinDateTime)

    // AndroidX
    implementation("androidx.activity:activity-ktx:1.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Room
//    implementation(libs.room.runtime)
//    implementation(libs.room.paging)
//    kapt(libs.room.compiler)

    // Compose
    implementation(libs.compose.compiler)
    implementation(libs.compose.ui)
    implementation(libs.compose.toolingPreview)
    debugImplementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.activity)
    implementation(libs.compose.icons)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")

    // Voyager
    implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
    implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
    implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:$voyagerVersion")
//    implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
    implementation("cafe.adriel.voyager:voyager-androidx:$voyagerVersion")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("de.nycode:retrofit2-kotlinx-serialization-converter:0.11.0")
    implementation("com.squareup.okhttp3:okhttp-tls:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Accompanist
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.swipeRefresh)
    implementation(libs.accompanist.systemUi)
    implementation(libs.accompanist.permissions)

    // Hilt
    implementation(libs.dagger.android)
    kapt(libs.dagger.compiler)
    kapt(libs.dagger.hilt.compiler)

    implementation("com.journeyapps:zxing-android-embedded:4.1.0") { isTransitive = false }
    implementation("com.google.zxing:core:3.4.0")

    implementation("com.marosseleng.android:compose-material3-datetime-pickers:+")
    implementation("io.github.boguszpawlowski.composecalendar:composecalendar:1.0.1")
    implementation("io.github.boguszpawlowski.composecalendar:kotlinx-datetime:1.0.1")
}
