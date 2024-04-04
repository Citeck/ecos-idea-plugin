package ru.citeck.ecos.codeinsight.forms.components;

import ru.citeck.ecos.index.indexers.AlfrescoModelIndexer;

import java.util.Set;

public abstract class PropertyComponent extends InputComponent {

    private static final Set<String> ARTIFACT_TYPES = Set.of(
            AlfrescoModelIndexer.PROPERTY
    );

    @Override
    public Set<String> getSupportedArtifactTypes() {
        return ARTIFACT_TYPES;
    }
}
