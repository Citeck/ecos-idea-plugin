package ru.citeck.idea.artifacts.action.codeinsight.forms.components

abstract class Container : ru.citeck.idea.artifacts.action.codeinsight.forms.components.Component() {
    var components: List<ru.citeck.idea.artifacts.action.codeinsight.forms.components.Component> = ArrayList()
}
