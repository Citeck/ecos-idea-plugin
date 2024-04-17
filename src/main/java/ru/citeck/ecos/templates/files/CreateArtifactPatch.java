package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.app.ArtifactPatch;

import java.util.List;

public class CreateArtifactPatch extends AbstractCreateEcosArtifactAction {
    public CreateArtifactPatch() {
        super("Artifact Patch", List.of("yaml", "json"), "ecos-artifact-patch", ArtifactPatch.PATH);
    }
}
