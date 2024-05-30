package tech.stonks.kvizard

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import org.jetbrains.plugins.gradle.service.project.GradleAutoImportAware
import org.jetbrains.plugins.gradle.service.project.open.linkAndRefreshGradleProject
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
import tech.stonks.kvizard.generator.KtorKoinAnnotTreeGenerator
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
            if (projectType == KVisionProjectType.FRONTEND_ONLY) {
                RunConfigurationUtil.createFrontendConfiguration(modifiableRootModel.project)
            } else {
                RunConfigurationUtil.createFullstackConfiguration(modifiableRootModel.project)
            }
            GradleAutoImportAware()
            invokeLater {
                val projectFilePath = root.canonicalPath + "/build.gradle.kts"
                linkAndRefreshGradleProject(projectFilePath, modifiableRootModel.project)
            }
        }
    }

    private fun createGenerator(): TreeGenerator {
        return when (projectType) {
            KVisionProjectType.FRONTEND_ONLY -> FrontendTreeGenerator()
            KVisionProjectType.KTOR_KOIN -> KtorKoinTreeGenerator()
            KVisionProjectType.KTOR_KOIN_ANNOT -> KtorKoinAnnotTreeGenerator()
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
                kVision = "7.5.1",
                kotlin = "1.9.24",
                coroutines = "1.8.1",
                templateJooby = TemplateJooby("3.1.2"),
                templateKtor = TemplateKtor(ktor = "2.3.11", koinAnnotations = "1.3.1"),
                templateMicronaut = TemplateMicronaut(micronaut = "4.4.3", micronautPlugins = "4.4.0"),
                templateSpring = TemplateSpring(springBoot = "3.3.0"),
                templateVertx = TemplateVertx(vertxPlugin = "1.4.0"),
                modules = emptyList()
            )
        }
    }
}
