package tech.stonks.kvizard

import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class KVisionModuleType : ModuleType<KVisionModuleBuilder>("KVISION_WIZARD") {

    private val _icon: Icon by lazy { IconLoader.getIcon("/images/kvision.png", KVisionModuleType::class.java) }

    override fun createModuleBuilder(): KVisionModuleBuilder {
        return KVisionModuleBuilder()
    }

    override fun getName(): String {
        return "KVision"
    }

    override fun getDescription(): String {
        return "A new project with KVision - an object oriented web framework for Kotlin/JS"
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return _icon
    }

    override fun getIcon(): Icon {
        return _icon
    }
}

enum class KVisionProjectType(val displayName: String) {
    FRONTEND_ONLY("Frontend project"),
    KTOR("Fullstack project with Ktor"),
    SPRING_BOOT("Fullstack project with Spring Boot"),
    JAVALIN("Fullstack project with Javalin"),
    JOOBY("Fullstack project with Jooby"),
    MICRONAUT("Fullstack project with Micronaut"),
    QUARKUS("Fullstack project with Quarkus"),
    VERTX("Fullstack project with Vert.x"),
}

val supportedProjectTypes = arrayOf(
    KVisionProjectType.FRONTEND_ONLY,
    KVisionProjectType.KTOR,
    KVisionProjectType.SPRING_BOOT,
    KVisionProjectType.JAVALIN,
    KVisionProjectType.JOOBY,
    KVisionProjectType.MICRONAUT,
    KVisionProjectType.QUARKUS,
    KVisionProjectType.VERTX
)
