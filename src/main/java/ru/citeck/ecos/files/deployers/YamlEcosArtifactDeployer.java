package ru.citeck.ecos.files.deployers;

import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;

public class YamlEcosArtifactDeployer extends AbstractEcosArtifactDeployer{

    @Override
    public String getMimeType() {
        return "application/x-yaml";
    }
    @Override
    public Class<? extends EcosArtifact> getFileType() {
        return YamlEcosArtifact.class;
    }

    @Override
    public String getMutationAttribute() {
        return "_self";
    }

}
