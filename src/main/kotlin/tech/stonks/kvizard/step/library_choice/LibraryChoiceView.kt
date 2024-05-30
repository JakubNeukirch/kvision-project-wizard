package tech.stonks.kvizard.step.library_choice

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.CheckBoxList
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.JBScrollPane
import tech.stonks.kvizard.KVisionProjectType
import tech.stonks.kvizard.data.model.Module
import tech.stonks.kvizard.supportedProjectTypes
import tech.stonks.kvizard.utils.setOnTextChangedListener
import java.awt.Dimension
import java.awt.FlowLayout
import java.util.function.Supplier
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.ListSelectionModel
import javax.swing.event.DocumentEvent


class LibraryChoiceView(
    var projectType: KVisionProjectType,
    var groupId: String,
    var artifactId: String,
    var selectedModules: List<String>,
    var selectedInitializers: List<String>,
    val modules: List<Module>,
    parentDisposable: Disposable?
) : JPanel() {

    var onChanged: () -> Unit = {}
    val groupTextField: JTextField
    val artifactTextField: JTextField

    init {
        layout = FlowLayout(FlowLayout.LEFT)
        alignmentX = JComponent.LEFT_ALIGNMENT
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = JComponent.LEFT_ALIGNMENT
            add(JLabel("Project type:").apply { alignmentX = LEFT_ALIGNMENT })
            add(ComboBox(supportedProjectTypes.map { it.displayName }.toTypedArray()).apply {
                alignmentX = LEFT_ALIGNMENT
                setMinimumAndPreferredWidth(250)
                addItemListener { event: java.awt.event.ItemEvent ->
                    if (event.stateChange == java.awt.event.ItemEvent.SELECTED) {
                        projectType = KVisionProjectType.entries.find { it.displayName == event.item }!!
                        onChanged()
                    }
                }
            })
            add(JLabel("Group:").apply { alignmentX = LEFT_ALIGNMENT })
            add(JTextField(groupId).apply {
                groupTextField = this
                alignmentX = LEFT_ALIGNMENT
                setOnTextChangedListener {
                    groupId = it
                    onChanged()
                }
            })
            ComponentValidator(parentDisposable!!).withValidator(Supplier<ValidationInfo> {
                val groupIdValue = groupTextField.text
                if (StringUtil.isNotEmpty(groupIdValue)) {
                    if (validateGroupName(groupIdValue)) {
                        null
                    } else {
                        ValidationInfo(
                            "Invalid group name (only lowercase letters (a-z), numbers (0-9), periods (.) and underscores (_) are valid)",
                            groupTextField
                        )
                    }
                } else {
                    ValidationInfo("Value is required", groupTextField)
                }
            }).installOn(groupTextField)
            groupTextField.document.addDocumentListener(object : DocumentAdapter() {
                override fun textChanged(e: DocumentEvent) {
                    ComponentValidator.getInstance(groupTextField).ifPresent { v: ComponentValidator -> v.revalidate() }
                }
            })
            add(JLabel("Artifact:").apply { alignmentX = LEFT_ALIGNMENT })
            add(JTextField(artifactId).apply {
                artifactTextField = this
                alignmentX = LEFT_ALIGNMENT
                setOnTextChangedListener {
                    artifactId = it
                    onChanged()
                }
            })
            ComponentValidator(parentDisposable).withValidator(Supplier<ValidationInfo> {
                val artifactIdValue = artifactTextField.text
                if (StringUtil.isNotEmpty(artifactIdValue)) {
                    if (validateArtifactName(artifactIdValue)) {
                        null
                    } else {
                        ValidationInfo(
                            "Invalid artifact name (only lowercase letters (a-z), numbers (0-9) and underscores (_) are valid)",
                            artifactTextField
                        )
                    }
                } else {
                    ValidationInfo("Value is required", artifactTextField)
                }
            }).installOn(artifactTextField)
            artifactTextField.document.addDocumentListener(object : DocumentAdapter() {
                override fun textChanged(e: DocumentEvent) {
                    ComponentValidator.getInstance(artifactTextField)
                        .ifPresent { v: ComponentValidator -> v.revalidate() }
                }
            })
            add(JLabel("Optional modules:").apply { alignmentX = LEFT_ALIGNMENT })
            val list = CheckBoxList<Module>().apply {
                alignmentX = LEFT_ALIGNMENT
                selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                setItems(modules) {
                    "${it.description} (${it.name})"
                }
                selectedModules.forEach { name ->
                    modules.find { it.name == name }?.let {
                        setItemSelected(it, true)
                    }
                }
                setCheckBoxListListener { index, value ->
                    if (value) {
                        modules[index].excludes?.mapNotNull { name ->
                            modules.find { it.name == name }
                        }?.forEach {
                            setItemSelected(it, false)
                        }
                    }
                    selectedModules = modules.filter { isItemSelected(it) }.map { it.name }
                    selectedInitializers =
                        modules.filter { isItemSelected(it) }.flatMap { it.initializers ?: emptyList() }.distinct()
                    onChanged()
                }
            }
            add(JBScrollPane(list).apply {
                alignmentX = LEFT_ALIGNMENT
                preferredSize = Dimension(570, 400)
            })
            add(Box.createRigidArea(Dimension(0, 20)))
            add(JButton("Check KVision website").apply {
                this.addActionListener {
                    BrowserUtil.browse("https://kvision.io")
                }
            })
        }
        add(panel)
    }

    fun validateGroupName(groupName: String?): Boolean {
        return groupName?.matches("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)*$".toRegex()) ?: false
    }

    fun validateArtifactName(artifactName: String?): Boolean {
        return artifactName?.matches("^[a-z][a-z0-9_]*$".toRegex()) ?: false
    }
}
