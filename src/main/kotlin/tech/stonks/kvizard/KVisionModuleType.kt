package tech.stonks.kvizard

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.getProjectCacheFileName
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import tech.stonks.kvizard.step.library_choice.LibraryChoiceStep
import tech.stonks.kvizard.utils.*
import java.io.File
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

class KVisionModuleBuilder : ModuleBuilder() {

    var backendLibrary: KVisionBackendLibrary = KVisionBackendLibrary.KTOR
    var groupId: String = "com.example"
    var artifactId: String = "project"

    companion object {
        private val rootFiles = arrayOf(
            "build.gradle.kts",
            "settings.gradle.kts",
            ".gettext.json",
            ".gitignore",
            "app.json",
            "gradle.properties",
            "system.properties",
            "gradlew.bat",
            "gradlew",
            "Procfile"
        )
        private val webpackFiles = arrayOf(
            "bootstrap.js",
            "css.js",
            "file.js",
            "handlebars.js",
            "jquery.js",
            "minify.js",
            "moment.js",
            "webpack.js"
        )
        private val backendResourcesFiles = arrayOf(
            "application.conf",
            "logback.xml"
        )
        private val backendFiles = arrayOf(
            "Main.kt",
            "Service.kt"
        )
        private val commonFiles = arrayOf(
            "Service.kt"
        )
        private val frontendSourceFiles = arrayOf(
            "App.kt",
            "Model.kt"
        )
        private val frontendWebFiles = arrayOf(
            "index.html"
        )
        private val frontendResourcesFiles = arrayOf(
            "messages.pot",
            "messages-en.po",
            "messages-pl.po"
        )
        private val frontendTestFiles = arrayOf(
            "AppSpec.kt"
        )
    }

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        val packageSegments = groupId
            .split(".")
            .toMutableList()
            .apply { add(artifactId) }
            .toList()
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
        modifiableRootModel.project.backgroundTask("Setting up project") {
            try {
                val attrs = generateAttributes()
                root.build {
                    dir("src") {
                        dir("backendMain") {
                            dir("kotlin") {
                                packages(packageSegments) {
                                    backendFiles.forEach { fileName -> file(fileName, "backend_source_$fileName", attrs) }
                                }
                            }
                            dir("resources") {
                                backendResourcesFiles.forEach { fileName ->
                                    file(
                                        fileName,
                                        "backend_resources_$fileName",
                                        attrs
                                    )
                                }
                            }
                        }
                        dir("commonMain") {
                            dir("kotlin") {
                                packages(packageSegments) {
                                    commonFiles.forEach { fileName -> file(fileName, "common_$fileName", attrs) }
                                }
                            }
                        }
                        dir("frontendMain") {
                            dir("kotlin") {
                                packages(packageSegments) {
                                    frontendSourceFiles.forEach { fileName -> file(fileName, "frontend_source_$fileName", attrs) }
                                }
                            }
                            dir("web") {
                                frontendWebFiles.forEach { fileName -> file(fileName, "frontend_web_$fileName", attrs) }
                            }
                            dir("resources") {
                                dir("i18n") {
                                    frontendResourcesFiles.forEach { fileName -> file(fileName, "frontend_resources_$fileName", attrs) }
                                }
                            }
                        }
                        dir("frontendTest") {
                            dir("kotlin") {
                                dir("test") {
                                    packages(packageSegments) {
                                        frontendTestFiles.forEach { fileName -> file(fileName, "frontend_test_$fileName", attrs) }
                                    }
                                }
                            }
                        }
                    }
                    dir("gradle") {
                        dir("wrapper") {
                            //todo add wrapper files
                        }
                    }
                    dir("webpack.config.d") {
                        webpackFiles.forEach { fileName -> file(fileName, "webpack_${fileName}", attrs) }
                    }
                    rootFiles.forEach { fileName -> file(fileName, "root_$fileName", attrs) }
                }
                root.refresh(false, true)
            } catch (ex: Exception) {
                ex.printStackTrace()
                println(ex)
            }

        }
    }

    private fun generateAttributes(): Map<String, String> {
        return mapOf(
            TemplateAttributes.ARTIFACT_ID to artifactId,
            TemplateAttributes.GROUP_ID to groupId,
            TemplateAttributes.PACKAGE_NAME to "${groupId}.${artifactId}",
        )
    }

    private fun createAndGetRoot(): VirtualFile? {
        val path = contentEntryPath?.let { FileUtil.toSystemIndependentName(it) } ?: return null
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(File(path).apply { mkdirs() }.absolutePath)
    }

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