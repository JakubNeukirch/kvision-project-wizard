package tech.stonks.kvizard.generator

class SpringTreeGenerator : TreeGenerator(
        "spring",
        false,
        jvmFiles = arrayOf("Main.kt", "Service.kt"),
        jvmResourcesFiles = arrayOf("application.yml", "logback.xml")
)