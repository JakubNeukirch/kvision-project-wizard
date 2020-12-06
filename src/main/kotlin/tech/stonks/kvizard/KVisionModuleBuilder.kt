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
import tech.stonks.kvizard.generator.FrontendTreeGenerator
import tech.stonks.kvizard.generator.KtorTreeGenerator
import tech.stonks.kvizard.generator.SpringTreeGenerator
import tech.stonks.kvizard.generator.TreeGenerator
import tech.stonks.kvizard.step.library_choice.LibraryChoiceStep
import tech.stonks.kvizard.utils.KVisionDialogUtil
import tech.stonks.kvizard.utils.RunConfigurationUtil
import tech.stonks.kvizard.utils.backgroundTask
import tech.stonks.kvizard.utils.runGradle
import java.io.File

class KVisionModuleBuilder : ModuleBuilder() {

    companion object {
        /**
         * Here add libraries that were newly supported
         */
        val supportedProjectTypes = arrayOf(
            KVisionProjectType.FRONTEND_ONLY,
            KVisionProjectType.KTOR,
            KVisionProjectType.SPRING_BOOT
        )
    }

    var projectType: KVisionProjectType = KVisionProjectType.FRONTEND_ONLY
    var groupId: String = "com.example"
    var artifactId: String = "project"

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
                generator.generate(root, artifactId, groupId)
            } catch (ex: Exception) {
            }
            installGradleWrapper(modifiableRootModel.project)
            if (projectType == KVisionProjectType.FRONTEND_ONLY) {
                RunConfigurationUtil.createFrontendConfiguration(modifiableRootModel.project)
            } else {
                runCompileMetadata(modifiableRootModel.project)
                RunConfigurationUtil.createFullstackConfiguration(modifiableRootModel.project)
            }
            KVisionDialogUtil.showNewsDialog()
        }
    }

    private fun runCompileMetadata(project: Project) {
        project.runGradle("compileKotlinMetadata")
    }

    private fun installGradleWrapper(project: Project) {
        project.runGradle("wrapper --gradle-version 6.7.1 --distribution-type all")
    }

    private fun createGenerator(): TreeGenerator {
        return when (projectType) {
            KVisionProjectType.KTOR -> KtorTreeGenerator()
            KVisionProjectType.FRONTEND_ONLY -> FrontendTreeGenerator()
            KVisionProjectType.SPRING_BOOT -> SpringTreeGenerator()
            else -> throw IllegalStateException("${projectType.name} is not supported yet.")
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
        return LibraryChoiceStep(this)
    }
}
