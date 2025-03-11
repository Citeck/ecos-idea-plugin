package ru.citeck.idea.codeinsight.forms.components;

import java.util.Set;

public abstract class PropertyComponent extends InputComponent {

    private static final Set<String> ARTIFACT_TYPES = Set.of(
        // TODO: Why alfresco here?
        //AlfrescoModelIndexer.PROPERTY
    );

    @Override
    public Set<String> getSupportedArtifactTypes() {
        return ARTIFACT_TYPES;
    }
}
