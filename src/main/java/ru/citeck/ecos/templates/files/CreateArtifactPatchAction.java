package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.ArtifactPatch;

import java.util.List;

public class CreateArtifactPatchAction extends AbstractCreateEcosArtifactAction {
    public CreateArtifactPatchAction() {
        super("Artifact Patch", List.of("yaml", "json"), "ecos-artifact-patch", ArtifactPatch.PATH);
    }
}
