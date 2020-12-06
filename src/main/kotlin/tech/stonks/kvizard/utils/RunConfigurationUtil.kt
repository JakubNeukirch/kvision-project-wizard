package tech.stonks.kvizard.utils

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import org.jetbrains.plugins.gradle.service.execution.GradleExternalTaskConfigurationType
import org.jetbrains.plugins.gradle.service.execution.GradleRunConfiguration
import javax.swing.Icon

class KVisionConfigurationFactory(val task: String, private val args: String = "") :
    ConfigurationFactory(GradleExternalTaskConfigurationType.getInstance()) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        val conf = GradleRunConfiguration(
            project,
            GradleExternalTaskConfigurationType.getInstance().factory,
            "Run $task"
        )
        conf.settings.externalProjectPath = project.basePath
        conf.settings.taskNames = listOf(task)
        conf.settings.scriptParameters = args
        return conf
    }

    override fun getName(): String = "Run $task"

    override fun getIcon(): Icon = IconLoader.getIcon("/images/logo16.png", KVisionConfigurationFactory::class.java)
}

class RunnerComparator : Comparator<RunnerAndConfigurationSettings> {
    override fun compare(o1: RunnerAndConfigurationSettings?, o2: RunnerAndConfigurationSettings?): Int {
        return when {
            o1?.factory is KVisionConfigurationFactory -> 1
            o2?.factory is KVisionConfigurationFactory -> -1
            else -> 0
        }
    }
}

object RunConfigurationUtil {
    fun createFrontendConfiguration(project: Project) {
        val runManager = RunManagerImpl.getInstanceImpl(project)
        runManager.addConfiguration(
            RunnerAndConfigurationSettingsImpl(
                RunManagerImpl.getInstanceImpl(project),
                KVisionConfigurationFactory("run", "-t").createTemplateConfiguration(project)
            )
        )
        runManager.setOrder(RunnerComparator())
        runManager.requestSort()
    }

    fun createFullstackConfiguration(project: Project) {
        val runManager = RunManagerImpl.getInstanceImpl(project)
        runManager.addConfiguration(
            RunnerAndConfigurationSettingsImpl(
                RunManagerImpl.getInstanceImpl(project),
                KVisionConfigurationFactory("backendRun").createTemplateConfiguration(project)
            )
        )
        runManager.addConfiguration(
            RunnerAndConfigurationSettingsImpl(
                RunManagerImpl.getInstanceImpl(project),
                KVisionConfigurationFactory("frontendRun", "-t").createTemplateConfiguration(project)
            )
        )
        runManager.setOrder(RunnerComparator())
        runManager.requestSort()
    }
}