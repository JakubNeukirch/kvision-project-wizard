package tech.stonks.kvizard.generator

class KtorKoinAnnotTreeGenerator: TreeGenerator(
    "ktorkoinannot",
    jvmResourcesFiles = arrayOf(
        "application.conf",
        "logback.xml"
    ),
    jvmFiles = arrayOf(
        "Main.kt",
        "Service.kt"
    )
)