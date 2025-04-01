package ru.citeck.idea.artifacts.action.codeinsight.forms.components

import ecos.com.fasterxml.jackson210.annotation.JsonProperty

abstract class Component {

    private var label = ""
    private var key = ""
    private var refreshOn: List<String> = ArrayList()

    abstract fun getType(): String

    open fun setLabel(label: String) {
        this.label = label
    }

    open fun getLabel(): String {
        return label
    }

    open fun setKey(key: String) {
        this.key = key
    }

    open fun getKey(): String {
        return key
    }

    open fun setRefreshOn(refreshOn: List<String>) {
        this.refreshOn = refreshOn
    }

    open fun getRefreshOn(): List<String> {
        return refreshOn
    }

    @get:JsonProperty("input")
    open val input: Boolean
        get() = false
}
