package ru.citeck.ecos.files.deployers;

import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.XmlEcosArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;

public class XmlEcosArtifactDeployer extends AbstractEcosArtifactDeployer{

    @Override
    public String getMimeType() {
        return "text/xml";
    }
    @Override
    public Class<? extends EcosArtifact> getFileType() {
        return XmlEcosArtifact.class;
    }

    @Override
    public String getMutationAttribute() {
        return "_content";
    }

}
