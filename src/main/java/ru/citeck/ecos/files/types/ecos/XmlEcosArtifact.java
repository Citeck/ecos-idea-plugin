package ru.citeck.ecos.files.types.ecos;

import ru.citeck.ecos.files.types.filters.FileExtensionFilter;

public abstract class XmlEcosArtifact extends AbstractEcosArtifact {
    public XmlEcosArtifact(String path, String sourceId) {
        super(path, FileExtensionFilter.XML, sourceId);
    }
}
