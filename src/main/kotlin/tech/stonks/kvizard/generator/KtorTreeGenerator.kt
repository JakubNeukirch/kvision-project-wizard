package tech.stonks.kvizard.generator

class KtorTreeGenerator: TreeGenerator(
    "ktor",
    backendResourcesFiles = arrayOf(
        "application.conf",
        "logback.xml"
    ),
    backendFiles = arrayOf(
        "Main.kt",
        "Service.kt"
    )
)