package ru.citeck.completetion.java

import ru.citeck.metadata.QName
import java.awt.Component
import javax.swing.JEditorPane
import javax.swing.JList
import javax.swing.ListCellRenderer


class QNameListCellRenderer: ListCellRenderer<QName> {


    override fun getListCellRendererComponent(
        list: JList<out QName>?,
        value: QName,
        index: Int,
        isSelected: Boolean,
        cellHasFocus: Boolean
    ): Component {
        return value.getComponent(cellHasFocus)
    }
}