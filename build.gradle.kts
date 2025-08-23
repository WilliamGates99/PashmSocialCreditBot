import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.10"
    application
    alias(libs.plugins.kotlin.serialization)
}

group = "com.xeniac"
version = "2.1.1"

kotlin {
    jvmToolchain(jdkVersion = 23)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_23)
    }
}

dependencies {
    // Kotlin JSON Serialization Library
    implementation(libs.kotlinx.serialization.json)

    // Kotlin Telegram Bot Library
    implementation(libs.bundles.telegram)

    // Ktor Client Library
    implementation(libs.bundles.ktor)

    // Exposed SQL library
    implementation(libs.bundles.exposed)

    // PostgreSQL Driver Library
    implementation(libs.postgresql)

    // Logback Classic Library for SLF4J
    implementation(libs.logback.classic)
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