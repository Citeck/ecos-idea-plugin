package ru.citeck.idea.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.event.KeyEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

class CiteckRootSettings : Configurable {

    private var originalOidcCallbackPort = 0

    private var portTextField: JTextField? = null

    override fun getDisplayName(): @ConfigurableName String {
        return "Citeck"
    }

    override fun createComponent(): JComponent {

        originalOidcCallbackPort = CiteckSettingsService.getInstance().getOidcCallbackPort()

        val portTextField = object : JBTextField() {
            override fun processKeyEvent(ev: KeyEvent) {
                val char: Char = ev.keyChar
                if (char.code in 32..126 && !char.isDigit()) {
                    return
                }
                super.processKeyEvent(ev)
            }
        }
        portTextField.text = originalOidcCallbackPort.toString()
        this.portTextField = portTextField

        return FormBuilder
            .createFormBuilder()
            .addLabeledComponent("OIDC callback port", portTextField)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun reset() {
        portTextField?.text = originalOidcCallbackPort.toString()
    }

    override fun isModified(): Boolean {
        return getCurrentPort() != originalOidcCallbackPort
    }

    override fun apply() {
        val newPort = getCurrentPort()
        CiteckSettingsService.getInstance().setOidcCallbackPort(newPort)
        originalOidcCallbackPort = newPort
        portTextField?.text = newPort.toString()
    }

    private fun getCurrentPort(): Int {
        return (portTextField?.text ?: "").toIntOrNull() ?: CiteckSettingsService.DEFAULT_OIDC_CALLBACK_PORT
    }
}
