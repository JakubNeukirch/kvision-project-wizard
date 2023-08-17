package tech.stonks.kvizard.generator

class JoobyTreeGenerator : TreeGenerator(
    "jooby",
    false,
    jvmFiles = arrayOf("Main.kt", "Service.kt"),
    jvmResourcesFiles = arrayOf("application.conf", "logback.xml")
)
