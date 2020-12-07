package tech.stonks.kvizard.step.library_choice

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.ComboBox
import tech.stonks.kvizard.CompilerBackend
import tech.stonks.kvizard.KVisionModuleBuilder
import tech.stonks.kvizard.KVisionProjectType
import tech.stonks.kvizard.utils.setOnTextChangedListener
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField


class LibraryChoiceView(
    var projectType: KVisionProjectType,
    var groupId: String,
    var artifactId: String,
    var compilerBackend: CompilerBackend
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
            add(Box.createRigidArea(Dimension(0, 20)))
            add(JButton("Check Kotlin.News").apply {
                background = Color(0xffe017)

                this.addActionListener {
                    BrowserUtil.browse("https://kotlin.news")
                }
            })
        }
        add(panel)
    }
}