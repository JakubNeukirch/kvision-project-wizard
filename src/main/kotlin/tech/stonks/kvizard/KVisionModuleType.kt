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
    KTOR_KOIN("Ktor/Koin fullstack project"),
    KTOR("Ktor/Guice fullstack project"),
    SPRING_BOOT("Spring Boot fullstack project"),
    JAVALIN("Javalin fullstack project"),
    JOOBY("Jooby fullstack project"),
    MICRONAUT("Micronaut fullstack project"),
    VERTX("Vert.x fullstack project");
}
