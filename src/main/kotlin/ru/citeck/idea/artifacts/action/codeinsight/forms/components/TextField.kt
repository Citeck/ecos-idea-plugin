package ru.citeck.idea.artifacts.action.codeinsight.forms.components

class TextField : PropertyComponent() {

    companion object {
        const val TYPE: String = "textfield"
    }

    override fun getType(): String {
        return TYPE
    }
}
