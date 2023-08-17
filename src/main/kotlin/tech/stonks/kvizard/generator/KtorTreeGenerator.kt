package tech.stonks.kvizard.generator

class KtorTreeGenerator: TreeGenerator(
    "ktor",
    jvmResourcesFiles = arrayOf(
        "application.conf",
        "logback.xml"
    ),
    jvmFiles = arrayOf(
        "Main.kt",
        "Service.kt"
    )
)