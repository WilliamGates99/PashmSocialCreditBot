import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.0.20"
    application
    alias(libs.plugins.kotlin.serialization)
}

group = "com.xeniac"
version = "2.0.2"

kotlin {
    jvmToolchain(jdkVersion = 22)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_22)
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json) // Kotlin JSON Serialization Library

    // Kotlin Telegram Bot Library
    implementation(libs.tgbotapi)
    implementation(libs.tgbotapi.core)
    implementation(libs.tgbotapi.api)
    implementation(libs.tgbotapi.utils)

    // Ktor Client Library
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp) // Ktor OkHttp Engine
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Exposed SQL library
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)

    // SQLite JDBC Library
    implementation(libs.sqlite.jdbc)
}

application {
    mainClass = "MainKt"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}