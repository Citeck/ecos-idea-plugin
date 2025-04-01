package ru.citeck.idea.artifacts.action.codeinsight.forms.components

import ecos.com.fasterxml.jackson210.annotation.JsonIgnore

class Column : Container() {

    companion object {
        const val TYPE: String = "column"
    }

    var index: Int = 0

    override fun getType(): String {
        return TYPE
    }

    override fun getKey(): String {
        return "column"
    }

    @JsonIgnore
    override fun getLabel(): String {
        return super.getLabel()
    }
}
