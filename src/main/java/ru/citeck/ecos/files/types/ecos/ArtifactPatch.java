package ru.citeck.ecos.files.types.ecos;

public interface ArtifactPatch extends EcosArtifact {

    String SOURCE_ID = "eapps/artifact-patch";
    String PATH = "/app/module-patch/";

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