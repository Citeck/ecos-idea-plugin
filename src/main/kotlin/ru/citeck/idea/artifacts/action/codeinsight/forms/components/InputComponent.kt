package ru.citeck.idea.artifacts.action.codeinsight.forms.components

import ecos.com.fasterxml.jackson210.annotation.JsonIgnore
import ru.citeck.idea.artifacts.action.codeinsight.forms.Properties

abstract class InputComponent : ru.citeck.idea.artifacts.action.codeinsight.forms.components.Component() {

    val optionalWhenDisabled = false
    val clearOnHide = false
    val properties = ru.citeck.idea.artifacts.action.codeinsight.forms.Properties()

    var disableInlineEdit = true

    override val input: Boolean
        get() = true

    @JsonIgnore
    abstract fun getSupportedArtifactTypes(): Set<String>
}
