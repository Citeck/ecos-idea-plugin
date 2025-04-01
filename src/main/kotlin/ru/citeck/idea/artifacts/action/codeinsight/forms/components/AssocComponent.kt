package ru.citeck.idea.artifacts.action.codeinsight.forms.components

abstract class AssocComponent : InputComponent() {

    companion object {
        private val ARTIFACT_TYPES = setOf<String>()
    }

    override fun getSupportedArtifactTypes(): Set<String> {
        return ARTIFACT_TYPES
    }
}
