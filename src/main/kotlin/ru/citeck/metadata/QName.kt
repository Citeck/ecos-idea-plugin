package ru.citeck.metadata

import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JEditorPane

class QName(
    val localName: String,
    val prefix: String,
    val jField: String,
    val jClass: String
) {

    companion object {
        const val CLASS = "org.alfresco.service.namespace.QName"
    }

    private fun toHtml(): String {
        return "<div style=\"padding:4px\"><b>$prefix:$localName</b><br/>$jClass.$jField<br/></div>"
    }

    private val component: JEditorPane = JEditorPane()
    private val border = BorderFactory.createLineBorder(Color.decode("#2E86C1"), 2, true)

    fun getComponent(hasFocus: Boolean): JEditorPane {
        if (hasFocus) {
            component.border = border
        } else {
            component.border = null
        }
        return component
    }

    override fun toString(): String {
        return "${prefix?:""}:$localName $jClass.$jField"
    }

    init {
        component.isEditable = false
        component.contentType = "text/html"
        component.text = toHtml()
    }

}