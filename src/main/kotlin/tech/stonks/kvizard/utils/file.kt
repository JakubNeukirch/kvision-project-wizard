package tech.stonks.kvizard.utils

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import java.io.File

fun File.dir(name: String, body: File.() -> Unit = {}) {
    val file = File(this, name)
    if(!file.exists() || !file.isDirectory) {
        file.mkdirs()
    }
    file.body()
}

fun File.packages(packages: List<String>, body: File.() -> Unit = {}) {
    val file = File(this, packages.joinToString("/"))
    if(!file.exists() || !file.isDirectory) {
        file.mkdirs()
    }
    file.body()
}

fun VirtualFile.build(body: File.() -> Unit = {}) {
    File(this.path).body()
}

fun File.file(name: String, resourceFileName: String) {
    val file = File(this, name)
    if (!file.exists()) {
        file.createNewFile()
    }
    val data = FileTemplateManager
        .getDefaultInstance()
        .getInternalTemplate(resourceFileName)
        .text
    file.writeText(data)
}

fun Project.getRootFile(): VirtualFile? {
    return projectFile?.parent?.parent
}