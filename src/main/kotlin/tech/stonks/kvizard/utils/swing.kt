package tech.stonks.kvizard.utils

import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

fun JTextField.setOnTextChangedListener(onTextChanged: (text: String) -> Unit) {
    this.document.addDocumentListener(object: DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) = Unit

        override fun removeUpdate(e: DocumentEvent?) = Unit

        override fun changedUpdate(e: DocumentEvent?) {
            onTextChanged(e?.document?.getText(0, document.length) ?: "")
        }
    })
}