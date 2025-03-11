package ru.citeck.idea.authentication.basic

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.Dimension
import javax.swing.JComponent

class PasswordInputDialog(project: Project) : DialogWrapper(project) {

    private val userName = JBTextField()
    private val password = JBPasswordField()

    init {
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val panel = FormBuilder
            .createFormBuilder()
            .addLabeledComponent("User name:", userName)
            .addLabeledComponent("Password:", password)
            .panel
        panel.minimumSize = Dimension(400, 0)
        return panel
    }

    fun getUserName(): String {
        return userName.text ?: ""
    }

    fun getPassword(): String {
        return String(password.password ?: charArrayOf())
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return userName
    }
}
