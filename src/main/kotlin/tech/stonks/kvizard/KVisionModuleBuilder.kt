package tech.stonks.kvizard

class KVisionModuleBuilder : BaseKVisionModuleBuilder(
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