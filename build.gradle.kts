import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("io.papermc.paperweight.userdev") version "1.3.3"
}

group = "de.stckoverflw"
version = "1.3.2"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    // PaperMC Dependency
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")

    // KSpigot dependency
    implementation("net.axay", "kspigot", "1.18.1")

    // ProtocolLib
    implementation("com.comphenix.protocol", "ProtocolLib", "4.7.0")

    // Gson dependency
    implementation("com.google.code.gson", "gson", "2.8.9")

    // Brigadier dependency
    implementation("com.mojang", "brigadier", "1.0.18")

    // Kotlinx.Coroutines dependency
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.0")
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.6.0")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    jar {
        // Disabled, because we use the shadowJar task for building our jar
        enabled = false
    }
    build {
        dependsOn(reobfJar)
        dependsOn(shadowJar)
    }
    // Relocating KSpigot to prevent conflicts with multiple Plugins using KSpigot
    shadowJar {
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}
