package tech.stonks.kvizard.generator

class KtorKoinTreeGenerator: TreeGenerator(
    "ktorkoin",
    backendResourcesFiles = arrayOf(
        "application.conf",
        "logback.xml"
    ),
    backendFiles = arrayOf(
        "Main.kt",
        "Service.kt"
    )
)