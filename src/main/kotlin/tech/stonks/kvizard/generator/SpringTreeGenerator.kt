package tech.stonks.kvizard.generator

class SpringTreeGenerator : TreeGenerator(
        "spring",
        false,
        backendFiles = arrayOf("Main.kt", "Service.kt"),
        backendResourcesFiles = arrayOf("application.yml", "logback.xml")
)