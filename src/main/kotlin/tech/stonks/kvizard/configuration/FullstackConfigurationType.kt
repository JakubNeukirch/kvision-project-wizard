package tech.stonks.kvizard.configuration

import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.icons.AllIcons
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel

class FullstackConfigurationType : ConfigurationType {
    override fun getDisplayName(): String = "Run Fullstack"

    override fun getConfigurationTypeDescription(): String = "Run backend & frontend of KVision app"

    override fun getIcon(): Icon = AllIcons.RunConfigurations.Application

    override fun getId(): String = "KVISION_FULLSTACK_CONFIGURATION"

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(FullstackConfigurationFactory(this))
    }
}

class FullstackConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return FullstackConfiguration(project, this, "Run Fullstack")
    }

    override fun getName(): String = "Fullstack run configuration factory"
}

class FullstackConfiguration(project: Project, factory: ConfigurationFactory, name: String) : RunConfigurationBase<Any>(project, factory, name) {
    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState? {
        return null /*RunProfileState { executor, runner ->
            project.runGradle
        }*/
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return FullstackEditor()
    }
}

class FullstackEditor : SettingsEditor<FullstackConfiguration>() {
    override fun resetEditorFrom(s: FullstackConfiguration) = Unit

    override fun applyEditorTo(s: FullstackConfiguration) = Unit

    override fun createEditor(): JComponent = JPanel()
}