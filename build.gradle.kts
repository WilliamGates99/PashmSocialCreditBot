import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0"
    application
}

group = "com.xeniac"
version = "1.6.0"

kotlin {
    jvmToolchain(jdkVersion = 21)
}

dependencies {
    // Kotlin Telegram Bot Library
    implementation(libs.kotlin.telegram.bot)

    // Google Gson Library
    implementation(libs.gson)

    // Square OkHttp Library
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Square Retrofit Library
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

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