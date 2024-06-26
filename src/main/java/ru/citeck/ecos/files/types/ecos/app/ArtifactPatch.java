package ru.citeck.ecos.files.types.ecos.app;

import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;

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

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/ecos_artifacts.html#id8";
    }

}
