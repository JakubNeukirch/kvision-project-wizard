package tech.stonks.kvizard.utils

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction
import tech.stonks.kvizard.KVisionModuleBuilder
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

fun File.file(
    name: String,
    templateName: String,
    attributes: Map<String, Any> = emptyMap(),
    binary: Boolean = false,
    executable: Boolean = false
) {
    val file = File(this, name)
    if (!file.exists()) {
        file.createNewFile()
    }
    if (!binary) {
        val data = getTemplateData(templateName, attributes)
        file.writeText(data)
    } else {
        val data = getBinaryData(templateName)
        file.writeBytes(data)
    }
    if (executable) {
        file.setExecutable(true)
    }
}

private fun getTemplateData(templateName: String, attributes: Map<String, Any> = emptyMap()): String {
    val template = FileTemplateManager
        .getDefaultInstance()
        .getInternalTemplate(templateName)
    return if (attributes.isEmpty()) {
        template.text
    } else {
        template.getText(attributes)
    }
}

private fun getBinaryData(templateName: String): ByteArray {
    KVisionModuleBuilder::class.java.getResourceAsStream("/fileTemplates/internal/$templateName")
        .use { stream ->
            return stream!!.readBytes()
        }
}

fun Project.runGradle(command: String) {
    GradleExecuteTaskAction.runGradle(this, DefaultRunExecutor.getRunExecutorInstance(), this.basePath!!, command)
}

fun Project.getRootFile(): VirtualFile? {
    return projectFile?.parent?.parent
}

fun String.insertAfter(after: Regex, insert: String): String {
    val last = after.find(this)?.range?.last
    return if (last != null) {
        buildString {
            append(this.substring(0, last + 1))
            appendLine(insert)
            appendLine(this.substring(last + 1))
        }
    } else {
        this
    }

}

object TemplateAttributes {
    const val GROUP_ID = "GROUP_ID"
    const val ARTIFACT_ID = "ARTIFACT_ID"
    const val PACKAGE_NAME = "PACKAGE_NAME"
}