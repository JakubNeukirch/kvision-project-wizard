package tech.stonks.kvizard.step.library_choice

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import tech.stonks.kvizard.KVisionModuleBuilder
import javax.swing.JComponent

class LibraryChoiceStep(private val _builder: KVisionModuleBuilder): ModuleWizardStep() {
    private val _view: LibraryChoiceView by lazy {
        LibraryChoiceView(
            _builder.backendLibrary, _builder.groupId, _builder.artifactId
        ).apply {
            onChanged = {
                updateDataModel()
            }
        }
    }
    override fun getComponent(): JComponent {
        return _view
    }

    override fun updateDataModel() {
        _builder.backendLibrary = _view.backendLibrary
        _builder.groupId = _view.groupId
        _builder.artifactId = _view.artifactId
    }
}