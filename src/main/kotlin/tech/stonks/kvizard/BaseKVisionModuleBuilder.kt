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
import tech.stonks.kvizard.step.library_choice.LibraryChoiceStep
import tech.stonks.kvizard.utils.*
import java.io.File

/**
 * Base class for building KVision project.
 * File name of template should look like: <project_type>_<directory>_<source_type>_<filename>.ft
 *  project_type - used only in backend depending files (that means: backend source dir, build.gradle)
 *  directory - options: frontend, backend - if destination dir is root, leave blank
 *  source_type - options: source, resources, test - if none of this, leave blank
 *  filename - destined file name, for example for source code it would be "MainApp.kt"
 * Examples:
 *  - ktor_backend_source_Main.kt.ft
 *  - ktor_backend_resources_application.conf.ft
 *  - frontend_test_AppSpec.kt.ft
 * @constructor accepts arrays of file names to be generated - those are not template file names,
 * standard names like = Main.kt or application.conf. Based on them template file names are constructed
 */
abstract class BaseKVisionModuleBuilder(
    /**
     * This is used to specify which files should be loaded. It is inserted to templated name as "project_type"
     */
    private val templateName: String,
    private val isFrontendOnly: Boolean = false,
    private val backendResourcesFiles: Array<String> = arrayOf(),
    private val backendFiles: Array<String> = arrayOf(),
    private val gradleFile: Array<String> = arrayOf(
        "build.gradle.kts",
        "settings.gradle.kts"
    ),
    private val rootFiles: Array<String> = arrayOf(
        ".gettext.json",
        "gradle.properties",
        ".gitignore",
        "app.json",
        "system.properties",
        "gradlew.bat",
        "gradlew",
        "Procfile"
    ),
    private val webpackFiles: Array<String> = arrayOf(
        "bootstrap.js",
        "css.js",
        "file.js",
        "handlebars.js",
        "jquery.js",
        "minify.js",
        "moment.js",
        "webpack.js"
    ),
    private val commonFiles: Array<String> = arrayOf(
        "Service.kt"
    ),
    private val frontendSourceFiles: Array<String> = arrayOf(
        "App.kt",
        "Model.kt"
    ),
    private val frontendWebFiles: Array<String> = arrayOf(
        "index.html"
    ),
    private val frontendResourcesFiles: Array<String> = arrayOf(
        "messages.pot",
        "messages-en.po",
        "messages-pl.po"
    ),
    private val frontendTestFiles: Array<String> = arrayOf("AppSpec.kt"),
    private val ideaFiles: Array<String> = arrayOf("gradle.xml"),
) : ModuleBuilder() {

    var backendLibrary: KVisionBackendLibrary = KVisionBackendLibrary.KTOR
    var groupId: String = "com.example"
    var artifactId: String = "project"

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
                        if(!isFrontendOnly) {
                            dir("backendMain") {
                                dir("kotlin") {
                                    packages(packageSegments) {
                                        backendFiles.forEach { fileName ->
                                            file(
                                                fileName,
                                                "${templateName}_backend_source_$fileName",
                                                attrs
                                            )
                                        }
                                    }
                                }
                                dir("resources") {
                                    backendResourcesFiles.forEach { fileName ->
                                        file(
                                            fileName,
                                            "${templateName}_backend_resources_$fileName",
                                            attrs
                                        )
                                    }
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
                                    frontendSourceFiles.forEach { fileName ->
                                        file(
                                            fileName,
                                            "frontend_source_$fileName",
                                            attrs
                                        )
                                    }
                                }
                            }
                            dir("web") {
                                frontendWebFiles.forEach { fileName -> file(fileName, "frontend_web_$fileName", attrs) }
                            }
                            dir("resources") {
                                dir("i18n") {
                                    frontendResourcesFiles.forEach { fileName ->
                                        file(
                                            fileName,
                                            "frontend_resources_$fileName",
                                            attrs
                                        )
                                    }
                                }
                            }
                        }
                        dir("frontendTest") {
                            dir("kotlin") {
                                dir("test") {
                                    packages(packageSegments) {
                                        frontendTestFiles.forEach { fileName ->
                                            file(
                                                fileName,
                                                "frontend_test_$fileName",
                                                attrs
                                            )
                                        }

                                    }
                                }
                            }
                        }
                    }
                    dir("gradle") {
                        dir("wrapper") {
                        }
                    }
                    dir("idea.") {
                        ideaFiles.forEach { fileName -> file(fileName, "idea_${fileName}", attrs) }
                    }
                    dir("webpack.config.d") {
                        webpackFiles.forEach { fileName -> file(fileName, "webpack_${fileName}", attrs) }
                    }
                    gradleFile.forEach { fileName -> file(fileName, "${templateName}_${fileName}", attrs) }
                    rootFiles.forEach { fileName -> file(fileName, fileName, attrs) }
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