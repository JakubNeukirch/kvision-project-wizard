package tech.stonks.kvizard

import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class KVisionModuleType : ModuleType<KVisionModuleBuilder>("KVISION_WIZARD") {

    private val _icon: Icon by lazy { IconLoader.getIcon("/images/logo16.png") }

    override fun createModuleBuilder(): KVisionModuleBuilder {
        return KVisionModuleBuilder()
    }

    override fun getName(): String {
        return "KVision Project"
    }

    override fun getDescription(): String {
        return "KVision Project wizard. It is a fullstack kotlin framework"
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return _icon
    }

    override fun getIcon(): Icon {
        return _icon
    }
}

enum class KVisionBackendLibrary {
    KTOR, FRONTEND_ONLY, JAVALIN, JOOBY, MICRONAUT, SPRING_BOOT, VERTX;
}