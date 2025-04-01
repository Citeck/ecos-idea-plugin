package ru.citeck.idea.artifacts.action.codeinsight.forms.components

class TextArea : ru.citeck.idea.artifacts.action.codeinsight.forms.components.PropertyComponent() {

    companion object {
        const val TYPE: String = "textarea"
    }

    override fun getType(): String {
        return "textarea"
    }
}
