import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.1.20"
    application
    alias(libs.plugins.kotlin.serialization)
}

group = "com.xeniac"
version = "2.0.9"

kotlin {
    jvmToolchain(jdkVersion = 23)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_23)
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json) // Kotlin JSON Serialization Library

    // Kotlin Telegram Bot Library
    implementation(libs.bundles.telegram)

    // Ktor Client Library
    implementation(libs.bundles.ktor)

    // Exposed SQL library
    implementation(libs.bundles.exposed)

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