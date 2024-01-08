plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "com.xeniac"
version = "1.0.0"

kotlin {
    jvmToolchain(18)
}

dependencies {
    // Kotlin Telegram Bot Library
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")

    // Exposed SQL library
    implementation("org.jetbrains.exposed:exposed-core:0.45.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.45.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0")

    // SQLite JDBC Library
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
}