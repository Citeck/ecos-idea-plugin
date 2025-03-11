package ru.citeck.idea.settings.servers

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import com.intellij.util.ui.FormBuilder
import ru.citeck.idea.settings.CiteckSettingsService
import javax.swing.JComponent

class CiteckServersConfiguration : Configurable {

    private lateinit var ecosServerTableModel: EcosServerTableModel
    private lateinit var originalItems: List<CiteckServer>

    override fun getDisplayName(): @ConfigurableName String {
        return "Servers"
    }

    override fun createComponent(): JComponent {

        this.originalItems = CiteckSettingsService.getInstance().getServers()
        this.ecosServerTableModel = EcosServerTableModel(cloneOriginalItems())

        return FormBuilder
            .createFormBuilder()
            .addLabeledComponentFillVertically("Servers", ecosServerTableModel.createComponent())
            .panel
    }

    override fun isModified(): Boolean {
        return ecosServerTableModel.isModified
    }

    override fun reset() {
        ecosServerTableModel.reset(cloneOriginalItems())
    }

    private fun cloneOriginalItems(): List<CiteckServer.Builder> {
        return originalItems.map { it.copy() }
    }

    override fun apply() {
        CiteckSettingsService.getInstance().setServers(ecosServerTableModel.apply().map { it.build() })
    }
}
