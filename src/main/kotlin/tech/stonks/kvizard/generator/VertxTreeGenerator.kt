package tech.stonks.kvizard.generator

class VertxTreeGenerator : TreeGenerator(
    "vertx",
    false,
    jvmFiles = arrayOf("Main.kt", "Service.kt"),
    jvmResourcesFiles = arrayOf("logback.xml")
)
