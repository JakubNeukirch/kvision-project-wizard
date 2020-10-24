package tech.stonks.kvizard

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.util.IconLoader
import tech.stonks.kvizard.step.library_choice.LibraryChoiceStep
import javax.swing.Icon
import javax.swing.ImageIcon

public class KVisionModuleType: ModuleType<KVisionModuleBuilder>("KVISION_WIZARD") {

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

class KVisionModuleBuilder: ModuleBuilder() {

    var backendLibrary: KVisionBackendLibrary = KVisionBackendLibrary.KTOR
    var groupId: String = "com.example"
    var artifactId: String = "project"

    override fun getModuleType(): ModuleType<*> {
        return KVisionModuleType()
    }

    override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable?): ModuleWizardStep? {
        return LibraryChoiceStep(this)
    }
}

enum class KVisionBackendLibrary {
    KTOR, JAVALIN, JOOBY, MICRONAUT, SPRING_BOOT, VERTX;
}