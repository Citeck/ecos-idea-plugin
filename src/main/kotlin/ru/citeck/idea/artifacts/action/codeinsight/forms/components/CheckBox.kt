package ru.citeck.idea.artifacts.action.codeinsight.forms.components

class CheckBox : PropertyComponent() {

    companion object {
        const val TYPE: String = "checkbox"
    }

    override fun getType(): String {
        return TYPE
    }
}
