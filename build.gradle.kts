plugins {
    kotlin("jvm") version "1.7.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("io.papermc.paperweight.userdev") version "1.3.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

group = "de.stckoverflw"
version = "1.4.1"

repositories {
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://libraries.minecraft.net")
}

dependencies {
    // PaperMC Dependency
    paperDevBundle("1.19-R0.1-SNAPSHOT")

    // KSpigot dependency
    implementation("net.axay", "kspigot", "1.19.0")

    // ProtocolLib
    implementation("com.comphenix.protocol", "ProtocolLib", "4.8.0")
}

kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    build {
        dependsOn(reobfJar)
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}

bukkit {
    name = "StckUtils"
    apiVersion = "1.19"
    authors = listOf(
        "StckOverflw",
        "l4zs",
    )
    main = "$group.stckutils.StckUtilsPlugin"
    website = "https://github.com/StckOverflwNet/StckUtils/"
    version = getVersion().toString()
    libraries = listOf(
        "net.axay:kspigot:1.19.0",
    )
    softDepend = listOf(
        "ProtocolLib",
    )
}
