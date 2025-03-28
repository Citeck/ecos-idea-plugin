package ru.citeck.idea.files.types.citeck;

import ru.citeck.idea.files.types.filters.FileExtensionFilter;

public abstract class XmlEcosArtifact extends AbstractEcosArtifact {

    public XmlEcosArtifact(String path, String sourceId) {
        super(path, FileExtensionFilter.XML, sourceId);
    }

}
