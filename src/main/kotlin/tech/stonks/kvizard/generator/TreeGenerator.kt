package tech.stonks.kvizard.generator

import com.intellij.openapi.vfs.VirtualFile
import tech.stonks.kvizard.data.model.VersionData
import tech.stonks.kvizard.utils.TemplateAttributes
import tech.stonks.kvizard.utils.build
import tech.stonks.kvizard.utils.dir
import tech.stonks.kvizard.utils.file
import tech.stonks.kvizard.utils.packages

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
abstract class TreeGenerator(
    /**
     * This is used to specify which files should be loaded. It is inserted to templated name as "project_type"
     */
    private val templateName: String,
    private val isFrontendOnly: Boolean = false,
    private val backendResourcesFiles: Array<String> = arrayOf(),
    private val backendResourcesAssetsFiles: Array<String> = arrayOf(),
    private val backendFiles: Array<String> = arrayOf(),
    private val gradleFile: Array<String> = arrayOf(
        "build.gradle.kts",
        "gradle.properties",
        "settings.gradle.kts"
    ),
    private val rootFiles: Array<String> = arrayOf(
        ".gettext.json",
        ".gitignore"
    ),
    private val webpackFiles: Array<String> = arrayOf(
        "bootstrap.js",
        "css.js",
        "file.js",
        "handlebars.js"
    ),
    private val webpackCustomFiles: Array<String> = arrayOf(
        "webpack.js"
    ),
    private val commonFiles: Array<String> = arrayOf(
        "Service.kt"
    ),
    private val frontendSourceFrontendFiles: Array<String> = arrayOf(
        "App.kt"
    ),
    private val frontendSourceFullstackFiles: Array<String> = arrayOf(
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
) {
    fun generate(
        root: VirtualFile,
        artifactId: String,
        groupId: String,
        modules: List<String>,
        initializers: List<String>,
        versionData: VersionData
    ) {
        try {
            val packageSegments = groupId
                .split(".")
                .toMutableList()
                .apply { add(artifactId) }
                .toList()
            val attrs = generateAttributes(artifactId, groupId, modules, initializers, versionData, isFrontendOnly)
            root.build {
                dir("src") {
                    if (!isFrontendOnly) {
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
                                if (backendResourcesAssetsFiles.isNotEmpty()) {
                                    dir("assets") {
                                        backendResourcesAssetsFiles.forEach { fileName ->
                                            file(
                                                fileName,
                                                "${templateName}_backend_resources_assets_$fileName",
                                                attrs
                                            )
                                        }
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
                    }
                    val frontendMain = if (isFrontendOnly) "main" else "frontendMain"
                    dir(frontendMain) {
                        dir("kotlin") {
                            packages(packageSegments) {
                                if (isFrontendOnly) {
                                    frontendSourceFrontendFiles.forEach { fileName ->
                                        file(
                                            fileName,
                                            "frontend_source_frontend_$fileName",
                                            attrs
                                        )
                                    }
                                } else {
                                    frontendSourceFullstackFiles.forEach { fileName ->
                                        file(
                                            fileName,
                                            "frontend_source_fullstack_$fileName",
                                            attrs
                                        )
                                    }
                                }
                            }
                        }
                        dir("web") {
                            frontendWebFiles.forEach { fileName -> file(fileName, "frontend_web_$fileName", attrs) }

                        }
                        if (modules.contains("kvision-i18n")) {
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
                    }
                    val frontendTest = if (isFrontendOnly) "test" else "frontendTest"
                    dir(frontendTest) {
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
                dir(".idea") {
                    ideaFiles.forEach { fileName -> file(fileName, "idea_${fileName}", attrs) }
                }
                dir("webpack.config.d") {
                    webpackFiles.forEach { fileName -> file(fileName, "webpack_${fileName}", attrs) }
                    val customPrefix = if (isFrontendOnly) "frontend" else "fullstack"
                    webpackCustomFiles.forEach { fileName ->
                        file(fileName, "webpack_${customPrefix}_${fileName}", attrs)
                    }
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

    private fun generateAttributes(
        artifactId: String,
        groupId: String,
        modules: List<String>,
        initializers: List<String>,
        versionData: VersionData,
        isFrontendOnly: Boolean
    ): Map<String, Any> {
        return mapOf(
            TemplateAttributes.ARTIFACT_ID to artifactId,
            TemplateAttributes.GROUP_ID to groupId,
            TemplateAttributes.PACKAGE_NAME to "${groupId}.${artifactId}",
            "kotlin_version" to versionData.kotlin,
            "ktor_version" to versionData.templateKtor.ktor,
            "serialization_version" to versionData.serialization,
            "kvision_version" to versionData.kVision,
            "coroutines_version" to versionData.coroutines,
            "jooby_version" to versionData.templateJooby.jooby,
            "micronaut_version" to versionData.templateMicronaut.micronaut,
            "spring_boot_version" to versionData.templateSpring.springBoot,
            "vertx_plugin_version" to versionData.templateVertx.vertxPlugin,
            "selected_modules" to modules,
            "selected_initializers" to initializers,
            "i18n_included" to modules.contains("kvision-i18n"),
            "frontend_only" to isFrontendOnly
        )
    }
}
