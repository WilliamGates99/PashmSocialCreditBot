import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "com.xeniac"
version = "1.4.2"

kotlin {
    jvmToolchain(jdkVersion = 21)
}

dependencies {
    // Kotlin Telegram Bot Library
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")

    // Google Gson Library
    implementation("com.google.code.gson:gson:2.10.1")

    // Square OkHttp Library
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // Square Retrofit Library
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Exposed SQL library
    implementation("org.jetbrains.exposed:exposed-core:0.45.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.45.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0")

    // SQLite JDBC Library
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
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