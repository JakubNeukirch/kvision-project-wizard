package tech.stonks.kvizard.step.library_choice

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.CheckBoxList
import com.intellij.ui.components.JBScrollPane
import tech.stonks.kvizard.CompilerBackend
import tech.stonks.kvizard.KVisionModuleBuilder
import tech.stonks.kvizard.KVisionProjectType
import tech.stonks.kvizard.data.model.Module
import tech.stonks.kvizard.utils.setOnTextChangedListener
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.ListSelectionModel


class LibraryChoiceView(
    var projectType: KVisionProjectType,
    var groupId: String,
    var artifactId: String,
    var compilerBackend: CompilerBackend,
    var selectedModules: List<String>,
    val modules: List<Module>
) : JPanel() {

    var onChanged: () -> Unit = {}

    init {
        layout = FlowLayout(FlowLayout.LEFT)
        alignmentX = JComponent.LEFT_ALIGNMENT
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = JComponent.LEFT_ALIGNMENT
            add(JLabel("Project type:").apply { alignmentX = LEFT_ALIGNMENT })
            add(ComboBox(KVisionModuleBuilder.supportedProjectTypes.map { it.displayName }.toTypedArray()).apply {
                alignmentX = LEFT_ALIGNMENT
                setMinimumAndPreferredWidth(250)
                addItemListener { event: java.awt.event.ItemEvent ->
                    if (event.stateChange == java.awt.event.ItemEvent.SELECTED) {
                        projectType = KVisionProjectType.values().find { it.displayName == event.item }!!
                        onChanged()
                    }
                }
            })
            add(JLabel("GroupId:").apply { alignmentX = LEFT_ALIGNMENT })
            add(JTextField(groupId).apply {
                alignmentX = LEFT_ALIGNMENT
                setOnTextChangedListener {
                    groupId = it
                    onChanged()
                }
            })
            add(JLabel("ArtifactId:").apply { alignmentX = LEFT_ALIGNMENT })
            add(JTextField(artifactId).apply {
                alignmentX = LEFT_ALIGNMENT
                setOnTextChangedListener {
                    artifactId = it
                    onChanged()
                }
            })
            add(JLabel("Kotlin/JS compiler backend:").apply { alignmentX = LEFT_ALIGNMENT })
            add(ComboBox(CompilerBackend.values().map { it.displayName }.toTypedArray()).apply {
                alignmentX = LEFT_ALIGNMENT
                setMinimumAndPreferredWidth(250)
                addItemListener { event: java.awt.event.ItemEvent ->
                    if (event.stateChange == java.awt.event.ItemEvent.SELECTED) {
                        compilerBackend = CompilerBackend.values().find { it.displayName == event.item }!!
                        onChanged()
                    }
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
                    onChanged()
                }
            }
            add(JBScrollPane(list).apply {
                alignmentX = LEFT_ALIGNMENT
                preferredSize = Dimension(570, 400)
            })
            add(Box.createRigidArea(Dimension(0, 20)))
            add(JButton("Check Kotlin.News").apply {
                this.addActionListener {
                    BrowserUtil.browse("https://kotlin.news")
                }
            })
        }
        add(panel)
    }
}
