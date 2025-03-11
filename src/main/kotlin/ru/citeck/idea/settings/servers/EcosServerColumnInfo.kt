package ru.citeck.idea.settings.servers

import com.intellij.openapi.util.NlsContexts.ColumnName
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.ColumnInfo
import javax.swing.DefaultCellEditor
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

internal class EcosServerColumnInfo(
    name: @ColumnName String,
    private val getter: (CiteckServer.Builder) -> String,
    private val setter: (CiteckServer.Builder, String) -> Unit
) : ColumnInfo<CiteckServer.Builder, String>(name) {

    private var editor: TableCellEditor = DefaultCellEditor(JBTextField())
    private var renderer: TableCellRenderer = DefaultTableCellRenderer()

    fun withEditor(editor: TableCellEditor): EcosServerColumnInfo {
        this.editor = editor
        return this
    }

    fun withRenderer(renderer: TableCellRenderer): EcosServerColumnInfo {
        this.renderer = renderer
        return this
    }

    override fun getEditor(citeckServer: CiteckServer.Builder): TableCellEditor {
        return editor
    }

    override fun valueOf(citeckServer: CiteckServer.Builder): String {
        return getter.invoke(citeckServer)
    }

    override fun isCellEditable(citeckServer: CiteckServer.Builder): Boolean {
        return true
    }

    override fun setValue(citeckServer: CiteckServer.Builder, value: String?) {
        setter.invoke(citeckServer, value ?: "")
    }

    override fun getRenderer(citeckServer: CiteckServer.Builder): TableCellRenderer {
        return renderer
    }
}
