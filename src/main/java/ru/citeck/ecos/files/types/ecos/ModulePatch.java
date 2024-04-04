package ru.citeck.ecos.files.types.ecos;

public interface ModulePatch extends EcosArtifact {

    String SOURCE_ID = "eapps/artifact-patch";
    String PATH = "/app/module-patch/";

    class JSON extends JsonEcosArtifact implements ModulePatch {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements ModulePatch {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
