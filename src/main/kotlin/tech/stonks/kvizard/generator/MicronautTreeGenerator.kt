package tech.stonks.kvizard.generator

class MicronautTreeGenerator : TreeGenerator(
    "micronaut",
    false,
    jvmFiles = arrayOf("Main.kt", "Service.kt"),
    jvmResourcesFiles = arrayOf("application.yml", "logback.xml")
)
