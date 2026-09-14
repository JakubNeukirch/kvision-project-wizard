package tech.stonks.kvizard.generator

class QuarkusTreeGenerator : TreeGenerator(
    "quarkus",
    false,
    jvmFiles = arrayOf("Application.kt", "Service.kt"),
    jvmResourcesMetaFiles = arrayOf("beans.xml"),
    subApplicationFiles = arrayOf("build.gradle.kts"),
    subApplicationSourceFiles = arrayOf("Main.kt"),
    subApplicationResourcesFiles = arrayOf("application.yml")
)
