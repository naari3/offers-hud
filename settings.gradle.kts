pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "Shedaniel"
            url = uri("https://maven.shedaniel.me/")
        }
        maven {
            name = "NeoForge"
            url = uri("https://maven.neoforged.net/releases")
        }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.2"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        version("1.21.11-fabric", "1.21.11")
        version("1.21.11-neoforge", "1.21.11")
        version("1.21.10-fabric", "1.21.10")
        version("1.21.10-neoforge", "1.21.10")
        version("1.21.9-fabric", "1.21.9")
        version("1.21.9-neoforge", "1.21.9")
        version("1.21.8-fabric", "1.21.8")
        version("1.21.8-neoforge", "1.21.8")
        version("1.21.5-fabric", "1.21.5")
        version("1.21.5-neoforge", "1.21.5")
        version("1.21.4-fabric", "1.21.4")
        version("1.21.4-neoforge", "1.21.4")
        version("1.21.1-fabric", "1.21.1")
        version("1.21.1-neoforge", "1.21.1")
        version("1.20.6-fabric", "1.20.6")
        version("1.20.6-neoforge", "1.20.6")
        vcsVersion = "1.21.11-fabric"
    }
}
