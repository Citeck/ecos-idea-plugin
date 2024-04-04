package ru.citeck.ecos.files.deployers;

import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;

public class JsonEcosArtifactDeployer extends AbstractEcosArtifactDeployer{

    @Override
    public String getMimeType() {
        return "application/json";
    }
    @Override
    public Class<? extends EcosArtifact> getFileType() {
        return JsonEcosArtifact.class;
    }

    @Override
    public String getMutationAttribute() {
        return "_self";
    }

}
