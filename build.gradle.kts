import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("io.papermc.paperweight.userdev") version "1.3.5"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

group = "de.stckoverflw"
version = "1.4.1"

repositories {
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    // PaperMC Dependency
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")

    // KSpigot dependency
    implementation("net.axay", "kspigot", "1.18.2")

    // ProtocolLib
    implementation("com.comphenix.protocol", "ProtocolLib", "4.8.0")

//    // Brigadier dependency
//    implementation("com.mojang", "brigadier", "1.0.18")
//
//    // Kotlinx.Coroutines dependency
//    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.0")
//    api("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.6.0")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    build {
        dependsOn(reobfJar)
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

bukkit {
    name = "StckUtils"
    apiVersion = "1.18"
    authors = listOf(
        "StckOverflw",
        "l4zs",
    )
    main = "$group.stckutils.StckUtilsPlugin"
    website = "https://github.com/StckOverflwNet/StckUtils/"
    version = getVersion().toString()
    libraries = listOf(
        "net.axay:kspigot:1.18.2",
    )
    softDepend = listOf(
        "ProtocolLib",
    )
}
