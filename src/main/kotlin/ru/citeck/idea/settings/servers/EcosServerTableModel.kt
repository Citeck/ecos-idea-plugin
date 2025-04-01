package ru.citeck.idea.settings.servers

import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.table.TableModelEditor
import ru.citeck.idea.view.table.PasswordCellRenderer
import javax.swing.DefaultCellEditor

internal class EcosServerTableModel(servers: List<CiteckServer.Builder>) : TableModelEditor<CiteckServer.Builder>(
        servers,
        COLUMNS,
        EcosServerCollectionItemEditor(),
        "No servers defined"
    ) {

    companion object {
        private val COLUMNS = arrayOf(
            EcosServerColumnInfo("Host (Mandatory)", { it.host }, CiteckServer.Builder::withHost),
            EcosServerColumnInfo("Name", { it.name }, CiteckServer.Builder::withName),
            EcosServerColumnInfo("Client ID", { it.clientId }, CiteckServer.Builder::withClientId),
            EcosServerColumnInfo("Client Secret", { it.clientSecret }, CiteckServer.Builder::withClientSecret)
                .withRenderer(PasswordCellRenderer())
                .withEditor(DefaultCellEditor(JBPasswordField()))
        )
    }

    init {
        helper.reset(servers)
    }
}
