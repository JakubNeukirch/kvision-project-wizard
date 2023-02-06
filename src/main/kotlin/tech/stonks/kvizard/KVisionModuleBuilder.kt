package tech.stonks.kvizard

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import tech.stonks.kvizard.data.VersionApi
import tech.stonks.kvizard.data.model.TemplateJooby
import tech.stonks.kvizard.data.model.TemplateKtor
import tech.stonks.kvizard.data.model.TemplateMicronaut
import tech.stonks.kvizard.data.model.TemplateSpring
import tech.stonks.kvizard.data.model.TemplateVertx
import tech.stonks.kvizard.data.model.VersionData
import tech.stonks.kvizard.generator.FrontendTreeGenerator
import tech.stonks.kvizard.generator.JavalinTreeGenerator
import tech.stonks.kvizard.generator.JoobyTreeGenerator
import tech.stonks.kvizard.generator.KtorKoinTreeGenerator
import tech.stonks.kvizard.generator.KtorTreeGenerator
import tech.stonks.kvizard.generator.MicronautTreeGenerator
import tech.stonks.kvizard.generator.SpringTreeGenerator
import tech.stonks.kvizard.generator.TreeGenerator
import tech.stonks.kvizard.generator.VertxTreeGenerator
import tech.stonks.kvizard.step.library_choice.LibraryChoiceStep
import tech.stonks.kvizard.utils.RunConfigurationUtil
import tech.stonks.kvizard.utils.backgroundTask
import tech.stonks.kvizard.utils.runGradle
import java.io.File

class KVisionModuleBuilder : ModuleBuilder() {

    val versionData by lazy { fetchVersionData() }

    companion object {
        val supportedProjectTypes = arrayOf(
            KVisionProjectType.FRONTEND_ONLY,
            KVisionProjectType.KTOR_KOIN,
            KVisionProjectType.KTOR,
            KVisionProjectType.SPRING_BOOT,
            KVisionProjectType.JAVALIN,
            KVisionProjectType.JOOBY,
            KVisionProjectType.MICRONAUT,
            KVisionProjectType.VERTX
        )
    }

    var projectType: KVisionProjectType = KVisionProjectType.FRONTEND_ONLY
    var groupId: String = "com.example"
    var artifactId: String = "project"
    var selectedModules: List<String> = listOf("kvision-bootstrap")
    var selectedInitializers: List<String> = listOf("BootstrapModule", "BootstrapCssModule")

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        val root = createAndGetRoot() ?: return
        modifiableRootModel.addContentEntry(root)
        try {
            ApplicationManager.getApplication().runWriteAction {
                val manager = PsiManager.getInstance(modifiableRootModel.project)
                manager.findFile(root)?.add(
                    PsiDirectoryFactory.getInstance(manager.project)
                        .createDirectory(root.createChildDirectory(null, "webpack"))
                )
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        val generator: TreeGenerator = createGenerator()
        modifiableRootModel.project.backgroundTask("Setting up project") {
            try {
                generator.generate(root, artifactId, groupId, selectedModules, selectedInitializers, versionData)
            } catch (ex: Exception) {
            }
            installGradleWrapper(modifiableRootModel.project)
            if (projectType == KVisionProjectType.FRONTEND_ONLY) {
                RunConfigurationUtil.createFrontendConfiguration(modifiableRootModel.project)
            } else {
                RunConfigurationUtil.createFullstackConfiguration(modifiableRootModel.project)
            }
        }
    }

    private fun installGradleWrapper(project: Project) {
        project.runGradle("wrapper --gradle-version 7.6 --distribution-type all")
    }

    private fun createGenerator(): TreeGenerator {
        return when (projectType) {
            KVisionProjectType.FRONTEND_ONLY -> FrontendTreeGenerator()
            KVisionProjectType.KTOR_KOIN -> KtorKoinTreeGenerator()
            KVisionProjectType.KTOR -> KtorTreeGenerator()
            KVisionProjectType.SPRING_BOOT -> SpringTreeGenerator()
            KVisionProjectType.JAVALIN -> JavalinTreeGenerator()
            KVisionProjectType.JOOBY -> JoobyTreeGenerator()
            KVisionProjectType.MICRONAUT -> MicronautTreeGenerator()
            KVisionProjectType.VERTX -> VertxTreeGenerator()
        }
    }

    private fun createAndGetRoot(): VirtualFile? {
        val path = contentEntryPath?.let { FileUtil.toSystemIndependentName(it) } ?: return null
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(File(path).apply { mkdirs() }.absolutePath)
    }

    override fun getModuleType(): ModuleType<*> {
        return KVisionModuleType()
    }

    override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable?): ModuleWizardStep {
        return LibraryChoiceStep(this, parentDisposable)
    }

    private fun fetchVersionData(): VersionData {
        return try {
            VersionApi.create().getVersionData().blockingGet()
        } catch (ex: Exception) {
            VersionData(
                kVision = "6.2.0",
                kotlin = "1.8.10",
                serialization = "1.4.1",
                coroutines = "1.6.4",
                templateJooby = TemplateJooby("2.16.1"),
                templateKtor = TemplateKtor("2.2.3"),
                templateMicronaut = TemplateMicronaut("3.8.3"),
                templateSpring = TemplateSpring(springBoot = "3.0.2"),
                templateVertx = TemplateVertx(vertxPlugin = "1.3.0"),
                modules = emptyList()
            )
        }
    }
}
