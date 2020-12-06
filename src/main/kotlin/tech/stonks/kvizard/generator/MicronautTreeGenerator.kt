package tech.stonks.kvizard.generator

class MicronautTreeGenerator : TreeGenerator(
    "micronaut",
    false,
    backendFiles = arrayOf("Main.kt", "Service.kt"),
    backendResourcesFiles = arrayOf("application.yml", "logback.xml")
)
