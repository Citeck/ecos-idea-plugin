package ru.citeck.idea.artifacts.action.codeinsight.forms.components

import ecos.com.fasterxml.jackson210.annotation.JsonProperty

class Panel : Container() {

    companion object {
        const val TYPE: String = "panel"
    }

    @JsonProperty("title")
    override fun setLabel(label: String) {
        super.setLabel(label)
    }

    @JsonProperty("title")
    override fun getLabel(): String {
        return super.getLabel()
    }

    override fun getType(): String {
        return TYPE
    }
}
