package tech.stonks.kvizard.utils

import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

fun JTextField.setOnTextChangedListener(onTextChanged: (text: String) -> Unit) {
    this.document.addDocumentListener(object : DocumentListener {
        override fun insertUpdate(e: DocumentEvent?) {
            onTextChanged(this@setOnTextChangedListener.text)
        }

        override fun removeUpdate(e: DocumentEvent?) {
            onTextChanged(this@setOnTextChangedListener.text)
        }

        override fun changedUpdate(e: DocumentEvent?) {
            onTextChanged(this@setOnTextChangedListener.text)
        }
    })
}