import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0"
    application
    alias(libs.plugins.kotlin.serialization)
}

group = "com.xeniac"
version = "1.6.2-Alpha"

kotlin {
    jvmToolchain(jdkVersion = 21)
}

dependencies {
    implementation(libs.kotlinx.serialization.json) // Kotlin JSON Serialization Library

    // Kotlin Telegram Bot Library
    implementation(libs.tgbotapi)
    implementation(libs.tgbotapi.core)
    implementation(libs.tgbotapi.api)
    implementation(libs.tgbotapi.utils)
    implementation(libs.kotlin.telegram.bot) // TODO: REMOVE

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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
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