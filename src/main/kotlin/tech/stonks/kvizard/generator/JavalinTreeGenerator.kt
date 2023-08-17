package tech.stonks.kvizard.generator

class JavalinTreeGenerator : TreeGenerator(
        "javalin",
        false,
        jvmFiles = arrayOf("Main.kt", "Service.kt"),
        jvmResourcesFiles = arrayOf("application.conf", "logback.xml"),
        jvmResourcesAssetsFiles = arrayOf(".placeholder")
)
