import org.gradle.jvm.tasks.Jar

plugins {
    id("dev.kikugie.stonecutter")
    id("dev.architectury.loom") version "1.14.473" apply false
    id("dev.architectury.loom-no-remap") version "1.14.473" apply false
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
val isUnobfuscated = stonecutter.current.version >= "26.1"

// Apply Architectury Loom - use no-remap for unobfuscated versions (26.1+)
if (isUnobfuscated) {
    apply(plugin = "dev.architectury.loom-no-remap")
} else {
    apply(plugin = "dev.architectury.loom")
}

extensions.configure<BasePluginExtension> {
    archivesName.set(property("archives_base_name") as String)
}
version = "${property("mod_version")}+${stonecutter.current.project}"
group = property("maven_group") as String

val javaInt = (findProperty("java_version") as String?)?.toIntOrNull() ?: 21

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
    if (!isUnobfuscated) {
        "mappings"(loom.layered {
            officialMojangMappings()
            val parchmentVersion = findProperty("parchment_version") as String?
            if (!parchmentVersion.isNullOrBlank()) {
                parchment("org.parchmentmc.data:parchment-${parchmentVersion}@zip")
            }
        })
    }

    val modImpl = if (isUnobfuscated) "implementation" else "modImplementation"
    val modApi = if (isUnobfuscated) "api" else "modApi"

    if (isFabric) {
        modImpl("net.fabricmc:fabric-loader:${property("loader_version")}")
        modImpl("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
        modImpl("com.terraformersmc:modmenu:${property("modmenu_version")}") {
            exclude(module = "fabric-api")
        }
        modApi("me.shedaniel.cloth:cloth-config-fabric:${property("cloth_config_version")}") {
            exclude(module = "fabric-api")
        }
    } else {
        "neoForge"("net.neoforged:neoforge:${property("neoforge_version")}")
        modImpl("me.shedaniel.cloth:cloth-config-neoforge:${property("cloth_config_version")}")
    }
}

val processResourcesVars: MutableMap<String, Any> = mutableMapOf(
    "version" to version.toString(),
    "java_version" to javaInt
)

if (isFabric) {
    val mcDepVersion = property("minecraft_deps") as String
    processResourcesVars["mc_dep_version"] = mcDepVersion

    tasks.named<ProcessResources>("processResources") {
        exclude("META-INF/mods.toml", "META-INF/neoforge.mods.toml")
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
        exclude("META-INF/mods.toml")
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(processResourcesVars)
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
    val javaObj = JavaVersion.toVersion(javaInt)
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
