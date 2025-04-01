package ru.citeck.idea.view.table

import javax.swing.table.DefaultTableCellRenderer

class PasswordCellRenderer : DefaultTableCellRenderer() {

    override fun setValue(value: Any?) {
        val len = value?.toString()?.length ?: 0
        super.setValue("*".repeat(len))
    }
}
