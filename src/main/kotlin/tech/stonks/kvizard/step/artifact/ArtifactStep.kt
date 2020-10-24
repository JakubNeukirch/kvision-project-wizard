package tech.stonks.kvizard.step.artifact

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import javax.swing.JComponent

class ArtifactStep: ModuleWizardStep() {
    private val _view: JComponent by lazy {
        ArtifactView()
    }

    override fun getComponent(): JComponent {
        return _view
    }

    override fun updateDataModel() {
        TODO("Not yet implemented")
    }
}