package tech.stonks.kvizard.step.library_choice

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.ui.ComboBox
import tech.stonks.kvizard.KVisionBackendLibrary
import tech.stonks.kvizard.KVisionModuleBuilder
import tech.stonks.kvizard.utils.setOnTextChangedListener
import java.awt.Color
import java.awt.FlowLayout
import javax.swing.*

class LibraryChoiceView(
    var backendLibrary: KVisionBackendLibrary,
    var groupId: String,
    var artifactId: String
) : JPanel() {

    var onChanged: () -> Unit = {}

    init {
        layout = FlowLayout(FlowLayout.LEFT)
        alignmentX = JComponent.LEFT_ALIGNMENT
        val panel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = JComponent.LEFT_ALIGNMENT
            add(JLabel("Choose your backend library").apply { alignmentX = LEFT_ALIGNMENT })
            add(ComboBox<KVisionBackendLibrary>(KVisionModuleBuilder.supportedBackendLibraries).apply {
                alignmentX = LEFT_ALIGNMENT
                prototypeDisplayValue = backendLibrary
                addItemListener { event: java.awt.event.ItemEvent ->
                    if (event.stateChange == java.awt.event.ItemEvent.SELECTED) {
                        backendLibrary = event.item as KVisionBackendLibrary
                        onChanged()
                    }
                }
            })
            add(JLabel("GroupId").apply { alignmentX = LEFT_ALIGNMENT })
            add(JTextField(groupId).apply {
                alignmentX = LEFT_ALIGNMENT
                setOnTextChangedListener {
                    groupId = it
                    onChanged()
                }
            })
            add(JLabel("ArtifactId").apply { alignmentX = LEFT_ALIGNMENT })
            add(JTextField(artifactId).apply {
                alignmentX = LEFT_ALIGNMENT
                setOnTextChangedListener {
                    artifactId = it
                    onChanged()
                }
            })
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