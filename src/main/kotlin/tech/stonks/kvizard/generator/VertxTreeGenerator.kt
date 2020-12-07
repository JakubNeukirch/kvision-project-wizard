package tech.stonks.kvizard.generator

class VertxTreeGenerator : TreeGenerator(
    "vertx",
    false,
    backendFiles = arrayOf("Main.kt", "Service.kt"),
    backendResourcesFiles = arrayOf("logback.xml")
)
