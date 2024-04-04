package ru.citeck.ecos.codeinsight.forms.components;

import ru.citeck.ecos.index.indexers.AlfrescoModelIndexer;

import java.util.Set;

public abstract class AssocComponent extends InputComponent {

    private static final Set<String> ARTIFACT_TYPES = Set.of(
            AlfrescoModelIndexer.ASSOCIATION,
            AlfrescoModelIndexer.CHILD_ASSOCIATION
    );

    @Override
    public Set<String> getSupportedArtifactTypes() {
        return ARTIFACT_TYPES;
    }

}
