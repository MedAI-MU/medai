import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleFirebaseAppdistribution)
    alias(libs.plugins.googleGmsGoogleServices)
    alias(libs.plugins.googleFirebaseCrashlytics)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)

            // LiteRT (TensorFlow Lite successor) for on-device ML supporting 16 KB page sizes
            implementation("com.google.ai.edge.litert:litert:1.4.0")
            implementation("com.google.ai.edge.litert:litert-support:1.4.0")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(compose.materialIconsExtended)
            implementation(libs.kotlinx.datetime)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            // Ktor Core + JSON
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.auth)

            implementation("cafe.adriel.voyager:voyager-navigator:1.1.0-beta03")
            implementation("cafe.adriel.voyager:voyager-tab-navigator:1.1.0-beta03")
            implementation("cafe.adriel.voyager:voyager-transitions:1.1.0-beta03")
            implementation("cafe.adriel.voyager:voyager-screenmodel:1.1.0-beta03")
            implementation(libs.voyager.koin)

            // data store
            implementation("androidx.datastore:datastore-preferences-core:1.1.1")

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.coroutines.test)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    debugImplementation(compose.uiTooling)
}

val generateBuildConfig = tasks.register("generateBuildConfig") {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        inputs.file(localPropertiesFile)
    }

    val outputDir = layout.buildDirectory.dir("generated/buildconfig/src/commonMain/kotlin")
    outputs.dir(outputDir)

    doLast {
        val localProperties = Properties()
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }
        val envUrl = System.getenv("MEDAI_BASE_URL")
        val propUrl = if (localPropertiesFile.exists()) localProperties.getProperty("medai.base_url") else null
        var baseUrl = envUrl ?: propUrl ?: "http://10.0.2.2:8000/api/"
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/"
        }
        if (!baseUrl.endsWith("api/")) {
            baseUrl += "api/"
        }

        val outputFile = outputDir.get().file("org/example/project/AppConfig.kt").asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            package org.example.project

            object AppConfig {
                const val BASE_URL = "$baseUrl"
            }
            """.trimIndent()
        )
    }
}

kotlin {
    sourceSets {
        commonMain {
            kotlin.srcDirs(generateBuildConfig)
        }
    }
}
