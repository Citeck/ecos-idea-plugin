package ru.citeck.idea.files.types.citeck.app;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

public interface ArtifactPatch extends EcosArtifact {

    String SOURCE_ID = "eapps/artifact-patch";
    String PATH = "/app/artifact-patch/";

    class JSON extends JsonEcosArtifact implements ArtifactPatch {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements ArtifactPatch {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
