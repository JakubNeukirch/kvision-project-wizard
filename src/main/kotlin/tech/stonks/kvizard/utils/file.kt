package tech.stonks.kvizard.utils

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction
import java.io.File

fun File.dir(name: String, body: File.() -> Unit = {}) {
    val file = File(this, name)
    if (!file.exists() || !file.isDirectory) {
        file.mkdirs()
    }
    file.body()
}

fun File.packages(packages: List<String>, body: File.() -> Unit = {}) {
    val file = File(this, packages.joinToString("/"))
    if (!file.exists() || !file.isDirectory) {
        file.mkdirs()
    }
    file.body()
}

fun VirtualFile.build(body: File.() -> Unit = {}) {
    File(this.path).body()
}

fun File.file(name: String, templateName: String, attributes: Map<String, String> = emptyMap()) {
    val file = File(this, name)
    if (!file.exists()) {
        file.createNewFile()
    }
    val data = getTemplateData(templateName, attributes)
    file.writeText(data)
}

private fun getTemplateData(templateName: String, attributes: Map<String, String> = emptyMap()): String {
    val template = FileTemplateManager
            .getDefaultInstance()
            .getInternalTemplate(templateName)
    return if (attributes.isEmpty()) {
        template.text
    } else {
        template.getText(attributes)
    }
}

fun Project.runGradle(command: String) {
    GradleExecuteTaskAction.runGradle(this, DefaultRunExecutor.getRunExecutorInstance(), this.basePath!!, command)
}

fun Project.getRootFile(): VirtualFile? {
    return projectFile?.parent?.parent
}

object TemplateAttributes {
    const val GROUP_ID = "GROUP_ID"
    const val ARTIFACT_ID = "ARTIFACT_ID"
    const val PACKAGE_NAME = "PACKAGE_NAME"
}