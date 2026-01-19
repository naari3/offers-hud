import org.gradle.jvm.tasks.Jar

plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.13-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("maven-publish")
}

val current = stonecutter.current.project
val isNeoForge = current.endsWith("-neoforge")
val isFabric = !isNeoForge

stonecutter {
    constants["neoforge"] = isNeoForge
    constants["fabric"] = isFabric
}

// Extract MC version from project name
val mcVersion = current.replace("-fabric", "").replace("-neoforge", "")

// Apply Architectury plugins
apply(plugin = "dev.architectury.loom")
apply(plugin = "architectury-plugin")

extensions.configure<dev.architectury.plugin.ArchitectPluginExtension> {
    if (isFabric) {
        fabric()
    } else {
        neoForge()
    }
}

extensions.configure<BasePluginExtension> {
    archivesName.set(property("archives_base_name") as String)
}
version = "${property("mod_version")}+${stonecutter.current.project}"
group = property("maven_group") as String

val javaInt = if (stonecutter.eval(mcVersion, ">=1.20.5")) 21 else 17

repositories {
    mavenCentral()
    maven { url = uri("https://maven.shedaniel.me/") }
    maven { url = uri("https://maven.terraformersmc.com/releases/") }
    maven { url = uri("https://maven.parchmentmc.org") }
    maven { url = uri("https://maven.neoforged.net/releases/") }
}

val loom = extensions.getByType<net.fabricmc.loom.api.LoomGradleExtensionAPI>()

loom.apply {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir("../../run")
    }
}

dependencies {
    "minecraft"("com.mojang:minecraft:$mcVersion")
    "mappings"(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${property("parchment_version")}@zip")
    })

    if (isFabric) {
        "modImplementation"("net.fabricmc:fabric-loader:${property("loader_version")}")
        "modImplementation"("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
        "modImplementation"("com.terraformersmc:modmenu:${property("modmenu_version")}") {
            exclude(module = "fabric-api")
        }
        "modApi"("me.shedaniel.cloth:cloth-config-fabric:${property("cloth_config_version")}") {
            exclude(module = "fabric-api")
        }
    } else {
        "neoForge"("net.neoforged:neoforge:${property("neoforge_version")}")
        "modImplementation"("me.shedaniel.cloth:cloth-config-neoforge:${property("cloth_config_version")}")
    }
}

val processResourcesVars: MutableMap<String, Any> = mutableMapOf(
    "version" to version.toString(),
    "java_version" to javaInt
)

if (isFabric) {
    exclude("META-INF/mods.toml", "META-INF/neoforge.mods.toml")
    val mcDepVersion = property("minecraft_deps") as String
    processResourcesVars["mc_dep_version"] = mcDepVersion

    tasks.named<ProcessResources>("processResources") {
        inputs.property("version", version)
        inputs.property("mc_dep_version", mcDepVersion)
        inputs.property("java_version", javaInt)

        filesMatching("fabric.mod.json") {
            expand(processResourcesVars)
        }
        filesMatching("offershud.mixins.json") {
            expand(processResourcesVars)
        }
    }
} else {
    processResourcesVars["mc_version"] = mcVersion
    processResourcesVars["mc_deps_version"] = (findProperty("mc_deps_version") ?: "[$mcVersion]").toString()
    processResourcesVars["neoforge_version_range"] = (findProperty("neoforge_version_range") ?: "[0,)").toString()
    processResourcesVars["loader_version_range"] = (findProperty("loader_version_range") ?: "[0,)").toString()

    tasks.named<ProcessResources>("processResources") {
        exclude("fabric.mod.json")
        // NeoForge 20.4.x (MC 1.20.4) uses mods.toml, 20.5+ uses neoforge.mods.toml
        if (stonecutter.eval(mcVersion, ">=1.20.5")) {
            filesMatching("META-INF/neoforge.mods.toml") {
                expand(processResourcesVars)
            }
            exclude("META-INF/mods.toml")
        } else {
            filesMatching("META-INF/mods.toml") {
                expand(processResourcesVars)
            }
            exclude("META-INF/neoforge.mods.toml")
        }
        filesMatching("offershud.mixins.json") {
            expand(processResourcesVars)
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaInt)
}

extensions.configure<JavaPluginExtension> {
    withSourcesJar()
    val javaObj = if (javaInt == 21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    sourceCompatibility = javaObj
    targetCompatibility = javaObj
}

val archivesBaseName = extensions.getByType<BasePluginExtension>().archivesName.get()

tasks.named<Jar>("jar") {
    from("LICENSE") {
        rename { "${it}_${archivesBaseName}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
    }
}
