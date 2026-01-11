plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.11-fabric" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledBuildFabric", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
    versions { _, version -> version.project.contains("-fabric") }
}

stonecutter registerChiseled tasks.register("chiseledBuildNeoForge", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
    versions { _, version -> version.project.contains("-neoforge") }
}
