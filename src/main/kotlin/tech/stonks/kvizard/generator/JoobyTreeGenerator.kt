package tech.stonks.kvizard.generator

class JoobyTreeGenerator : TreeGenerator(
    "jooby",
    false,
    backendFiles = arrayOf("Main.kt", "Service.kt"),
    backendResourcesFiles = arrayOf("application.conf", "logback.xml")
)
