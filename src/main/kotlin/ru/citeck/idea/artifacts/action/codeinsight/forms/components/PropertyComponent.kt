package ru.citeck.idea.artifacts.action.codeinsight.forms.components

abstract class PropertyComponent : InputComponent() {

    companion object {
        private val ARTIFACT_TYPES = setOf<String>(
            // TODO: Why alfresco here?
            //AlfrescoModelIndexer.PROPERTY
        )
    }

    override fun getSupportedArtifactTypes(): Set<String> {
        return ARTIFACT_TYPES
    }
}
