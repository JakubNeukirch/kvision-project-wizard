package tech.stonks.kvizard.step.library_choice

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.Disposable
import tech.stonks.kvizard.KVisionModuleBuilder
import javax.swing.JComponent

class LibraryChoiceStep(private val _builder: KVisionModuleBuilder, private val parentDisposable: Disposable?) : ModuleWizardStep() {
    private val _view: LibraryChoiceView by lazy {
        LibraryChoiceView(
            _builder.projectType,
            _builder.groupId,
            _builder.artifactId,
            _builder.selectedModules,
            _builder.selectedInitializers,
            _builder.versionData.modules,
            parentDisposable
        ).apply {
            onChanged = {
                updateDataModel()
            }
        }
    }

    override fun validate(): Boolean {
        return _view.validateGroupName(_view.groupTextField.text) && _view.validateArtifactName(_view.artifactTextField.text)
    }

    override fun getComponent(): JComponent {
        return _view
    }

    override fun updateDataModel() {
        _builder.projectType = _view.projectType
        _builder.groupId = _view.groupId
        _builder.artifactId = _view.artifactId
        _builder.selectedModules = _view.selectedModules
        _builder.selectedInitializers = _view.selectedInitializers
    }
}
