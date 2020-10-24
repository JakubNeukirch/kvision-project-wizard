package tech.stonks.kvizard

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleType
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

public class KVisionModuleType : ModuleType<KVisionModuleBuilder>("KVISION_WIZARD") {

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

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        val packageSegments = groupId
            .split(".")
            .toMutableList()
            .apply { add(artifactId) }
            .toList()
        val root = createAndGetRoot() ?: return
        modifiableRootModel.addContentEntry(root)
        try{
            ApplicationManager.getApplication().runWriteAction {
                val manager = PsiManager.getInstance(modifiableRootModel.project)
                manager.findFile(root)?.add(
                    PsiDirectoryFactory.getInstance(manager.project)
                        .createDirectory(root.createChildDirectory(null, "webpack"))
                )
            }
        } catch (ex:java.lang.Exception) {
            ex.printStackTrace()
        }
        modifiableRootModel.project.backgroundTask("Setting up project") {
            try {
                root.build {
                    dir("src") {
                        dir("backendMain") {
                            dir("kotlin") {
                                packages(packageSegments) {
                                    //todo add backend files
                                }
                            }
                            dir("resources") {
                                //todo add conf and logback.conf
                            }
                        }
                        dir("commonMain") {
                            dir("kotlin") {
                                packages(packageSegments)
                            }
                        }
                        dir("frontendMain") {
                            dir("kotlin") {
                                packages(packageSegments) {
                                    //todo add some hello world files
                                }
                            }
                        }
                        dir("frontendTest") {
                            dir("kotlin") {
                                dir("test") {
                                    packages(packageSegments)
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
                        //todo add webpack files
                    }

                    file(
                        "build.gradle.kts",
                        "build.gradle.kts",
                        mapOf(
                            TemplateAttributes.ARTIFACT_ID to artifactId,
                            TemplateAttributes.GROUP_ID to groupId,
                        )
                    )
                    //todo add root files
                }
                root.refresh(false, true)
            } catch (ex: Exception) {
                ex.printStackTrace()
                println(ex)
            }

        }
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