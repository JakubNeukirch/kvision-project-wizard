package tech.stonks.kvizard.generator

class JavalinTreeGenerator : TreeGenerator(
        "javalin",
        false,
        backendFiles = arrayOf("Main.kt", "Service.kt"),
        backendResourcesFiles = arrayOf("application.conf", "logback.xml"),
        backendResourcesAssetsFiles = arrayOf(".placeholder")
)
