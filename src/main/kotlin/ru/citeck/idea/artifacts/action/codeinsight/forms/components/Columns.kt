package ru.citeck.idea.artifacts.action.codeinsight.forms.components

import ecos.com.fasterxml.jackson210.annotation.JsonIgnore

class Columns(columnsCount: Int) : ru.citeck.idea.artifacts.action.codeinsight.forms.components.Component() {

    companion object {
        const val TYPE: String = "columns"
    }

    val columns: MutableList<ru.citeck.idea.artifacts.action.codeinsight.forms.components.Column> = ArrayList()
    val oneColumnInViewMode: Boolean = true

    init {
        for (i in 0..<columnsCount) {
            val column = ru.citeck.idea.artifacts.action.codeinsight.forms.components.Column()
            column.index = i
            columns.add(column)
        }
    }

    override fun getType(): String {
        return ru.citeck.idea.artifacts.action.codeinsight.forms.components.Columns.Companion.TYPE
    }

    @JsonIgnore
    override fun getLabel(): String {
        return super.getLabel()
    }
}
