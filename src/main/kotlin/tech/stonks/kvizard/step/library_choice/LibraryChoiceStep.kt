package tech.stonks.kvizard.step.library_choice

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import tech.stonks.kvizard.KVisionModuleBuilder
import javax.swing.JComponent

class LibraryChoiceStep(private val _builder: KVisionModuleBuilder): ModuleWizardStep() {
    private val _view: LibraryChoiceView by lazy {
        LibraryChoiceView().apply {
            onSubmit = {
                updateDataModel()
            }
        }
    }
    override fun getComponent(): JComponent {
        return _view
    }

    override fun updateDataModel() {
        _builder.backendLibrary = _view.backendLibrary
    }
}