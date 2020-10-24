package tech.stonks.kvizard.step.library_choice

import com.intellij.openapi.ui.ComboBox
import tech.stonks.kvizard.KVisionBackendLibrary
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*

class LibraryChoiceView : JComponent() {
    var backendLibrary: KVisionBackendLibrary? = null
        private set

    var onSubmit: () -> Unit = {}

    init {
        layout = GridLayout(3, 1)
        add(JLabel("Choose your backend library"))
        add(
            ComboBox<KVisionBackendLibrary>(KVisionBackendLibrary.values()).apply {
            addItemListener { event: java.awt.event.ItemEvent ->
                if(event.stateChange == java.awt.event.ItemEvent.SELECTED) {
                    backendLibrary = event.item as KVisionBackendLibrary
                }
            }
        }
        )
        add(
            JPanel(FlowLayout()).apply {
                add(JButton("Cancel"))
                add(JButton("Submit").apply {
                    this.addActionListener { onSubmit() }
                })
            }
        )
    }
}