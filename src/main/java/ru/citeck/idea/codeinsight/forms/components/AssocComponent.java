package ru.citeck.idea.codeinsight.forms.components;

import java.util.Set;

public abstract class AssocComponent extends InputComponent {

    private static final Set<String> ARTIFACT_TYPES = Set.of(
        // TODO: Why alfresco here?
        //AlfrescoModelIndexer.ASSOCIATION,
        //AlfrescoModelIndexer.CHILD_ASSOCIATION
    );

    @Override
    public Set<String> getSupportedArtifactTypes() {
        return ARTIFACT_TYPES;
    }

}
