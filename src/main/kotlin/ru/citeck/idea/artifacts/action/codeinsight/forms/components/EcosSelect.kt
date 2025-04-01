package ru.citeck.idea.artifacts.action.codeinsight.forms.components

class EcosSelect : PropertyComponent() {

    companion object {
        const val TYPE: String = "ecosSelect"
    }

    val data = mapOf("url" to "/citeck/ecos/records/query")
    val lazyLoad = false

    override fun getType(): String {
        return TYPE
    }
}
