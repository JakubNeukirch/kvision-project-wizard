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
 *  directory - options: js, jvm - if destination dir is root, leave blank
 *  source_type - options: source, resources, test - if none of this, leave blank
 *  filename - destined file name, for example for source code it would be "MainApp.kt"
 * Examples:
 *  - ktor_jvm_source_Main.kt.ft
 *  - ktor_jvm_resources_application.conf.ft
 *  - js_test_AppSpec.kt.ft
 * @constructor accepts arrays of file names to be generated - those are not template file names,
 * standard names like = Main.kt or application.conf. Based on them template file names are constructed
 */
abstract class TreeGenerator(
    /**
     * This is used to specify which files should be loaded. It is inserted to templated name as "project_type"
     */
    private val templateName: String,
    private val isFrontendOnly: Boolean = false,
    private val jvmResourcesFiles: Array<String> = arrayOf(),
    private val jvmResourcesAssetsFiles: Array<String> = arrayOf(),
    private val jvmFiles: Array<String> = arrayOf(),
    private val gradleFile: Array<String> = arrayOf(
        "build.gradle.kts",
        "gradle.properties",
        "settings.gradle.kts"
    ),
    private val rootFiles: Array<String> = arrayOf(
        ".gettext.json",
        ".gitignore",
        "gradlew",
        "gradlew.bat",
    ),
    private val webpackFiles: Array<String> = arrayOf(
        "bootstrap.js",
        "css.js",
        "file.js",
        "handlebars.js",
        "webpack.js"
    ),
    private val commonFiles: Array<String> = arrayOf(
        "Service.kt"
    ),
    private val jsSourceFrontendFiles: Array<String> = arrayOf(
        "App.kt"
    ),
    private val jsSourceFullstackFiles: Array<String> = arrayOf(
        "App.kt",
        "Model.kt"
    ),
    private val jsWebFiles: Array<String> = arrayOf(
        "index.html"
    ),
    private val jsResourcesFiles: Array<String> = arrayOf(
        "messages.pot",
        "messages-en.po",
        "messages-pl.po"
    ),
    private val jsTestFiles: Array<String> = arrayOf("AppSpec.kt"),
    private val ideaFiles: Array<String> = arrayOf("gradle.xml"),
    private val gradleWrapperFiles: Array<String> = arrayOf("gradle-wrapper.jar", "gradle-wrapper.properties")
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
                        dir("jvmMain") {
                            dir("kotlin") {
                                packages(packageSegments) {
                                    jvmFiles.forEach { fileName ->
                                        file(
                                            fileName,
                                            "${templateName}_jvm_source_$fileName",
                                            attrs
                                        )
                                    }
                                }
                            }
                            dir("resources") {
                                jvmResourcesFiles.forEach { fileName ->
                                    file(
                                        fileName,
                                        "${templateName}_jvm_resources_$fileName",
                                        attrs
                                    )
                                }
                                if (jvmResourcesAssetsFiles.isNotEmpty()) {
                                    dir("assets") {
                                        jvmResourcesAssetsFiles.forEach { fileName ->
                                            file(
                                                fileName,
                                                "${templateName}_jvm_resources_assets_$fileName",
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
                    dir("jsMain") {
                        dir("kotlin") {
                            packages(packageSegments) {
                                if (isFrontendOnly) {
                                    jsSourceFrontendFiles.forEach { fileName ->
                                        file(
                                            fileName,
                                            "js_source_frontend_$fileName",
                                            attrs
                                        )
                                    }
                                } else {
                                    jsSourceFullstackFiles.forEach { fileName ->
                                        file(
                                            fileName,
                                            "js_source_fullstack_$fileName",
                                            attrs
                                        )
                                    }
                                }
                            }
                        }
                        dir("web") {
                            jsWebFiles.forEach { fileName -> file(fileName, "js_web_$fileName", attrs) }

                        }
                        if (modules.contains("kvision-i18n")) {
                            dir("resources") {
                                dir("i18n") {
                                    jsResourcesFiles.forEach { fileName ->
                                        file(
                                            fileName,
                                            "js_resources_$fileName",
                                            attrs
                                        )
                                    }
                                }
                            }
                        }
                    }
                    dir("jsTest") {
                        dir("kotlin") {
                            dir("test") {
                                packages(packageSegments) {
                                    jsTestFiles.forEach { fileName ->
                                        file(
                                            fileName,
                                            "js_test_$fileName",
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
                        gradleWrapperFiles.forEach { fileName ->
                            file(
                                fileName,
                                "wrapper_$fileName",
                                attrs,
                                binary = true
                            )
                        }
                    }
                }
                dir(".idea") {
                    ideaFiles.forEach { fileName -> file(fileName, "idea_${fileName}", attrs) }
                }
                dir("webpack.config.d") {
                    webpackFiles.forEach { fileName -> file(fileName, "webpack_${fileName}", attrs) }
                }
                gradleFile.forEach { fileName -> file(fileName, "${templateName}_${fileName}", attrs) }
                rootFiles.forEach { fileName ->
                    file(
                        fileName,
                        fileName,
                        attrs,
                        binary = (fileName == "gradlew" || fileName == "gradlew.bat"),
                        executable = (fileName == "gradlew")
                    )
                }
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
            "koin_annotations_version" to versionData.templateKtor.koinAnnotations,
            "kvision_version" to versionData.kVision,
            "coroutines_version" to versionData.coroutines,
            "jooby_version" to versionData.templateJooby.jooby,
            "micronaut_version" to versionData.templateMicronaut.micronaut,
            "micronaut_plugins_version" to versionData.templateMicronaut.micronautPlugins,
            "spring_boot_version" to versionData.templateSpring.springBoot,
            "vertx_plugin_version" to versionData.templateVertx.vertxPlugin,
            "selected_modules" to modules,
            "selected_initializers" to initializers,
            "i18n_included" to modules.contains("kvision-i18n"),
            "frontend_only" to isFrontendOnly
        )
    }
}
