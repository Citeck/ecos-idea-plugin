package ru.citeck.idea.view.list

import javax.swing.DefaultListCellRenderer
import javax.swing.Icon

class ListCellRendererWithIcon(private val icon: Icon) : DefaultListCellRenderer() {

    private val iconTextGap = 4

    override fun getIcon(): Icon {
        return icon
    }

    override fun getIconTextGap(): Int {
        return iconTextGap
    }
}
