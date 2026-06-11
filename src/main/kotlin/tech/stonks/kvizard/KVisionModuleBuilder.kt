package tech.stonks.kvizard

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.plugins.gradle.service.project.GradleAutoImportAware
import org.jetbrains.plugins.gradle.service.project.open.linkAndSyncGradleProject
import tech.stonks.kvizard.data.VersionApi
import tech.stonks.kvizard.data.model.TemplateJooby
import tech.stonks.kvizard.data.model.TemplateKtor
import tech.stonks.kvizard.data.model.TemplateMicronaut
import tech.stonks.kvizard.data.model.TemplateSpring
import tech.stonks.kvizard.data.model.VersionData
import tech.stonks.kvizard.generator.FrontendTreeGenerator
import tech.stonks.kvizard.generator.JavalinTreeGenerator
import tech.stonks.kvizard.generator.JoobyTreeGenerator
import tech.stonks.kvizard.generator.KtorTreeGenerator
import tech.stonks.kvizard.generator.MicronautTreeGenerator
import tech.stonks.kvizard.generator.SpringTreeGenerator
import tech.stonks.kvizard.generator.TreeGenerator
import tech.stonks.kvizard.generator.VertxTreeGenerator
import tech.stonks.kvizard.step.library_choice.LibraryChoiceStep
import tech.stonks.kvizard.utils.RunConfigurationUtil
import tech.stonks.kvizard.utils.backgroundTask
import java.io.File

class KVisionModuleBuilder : ModuleBuilder() {

    val kvScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

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
            kvScope.launch {
                val projectFilePath = root.canonicalPath + "/build.gradle.kts"
                linkAndSyncGradleProject(modifiableRootModel.project, projectFilePath)
            }
        }
    }

    private fun createGenerator(): TreeGenerator {
        return when (projectType) {
            KVisionProjectType.FRONTEND_ONLY -> FrontendTreeGenerator()
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
                kvision = "9.6.0",
                kotlin = "2.4.0",
                coroutines = "1.11.0",
                ksp = "2.3.9",
                kiluaRpc = "0.0.45",
                logback = "1.5.34",
                templateJooby = TemplateJooby("4.5.2"),
                templateKtor = TemplateKtor(ktor = "3.5.0"),
                templateMicronaut = TemplateMicronaut(micronaut = "5.0.2", micronautPlugins = "5.0.0"),
                templateSpring = TemplateSpring(springBoot = "4.1.0"),
                modules = emptyList()
            )
        }
    }
}
