package ru.citeck.ecos.files.types.ecos;

public interface Config extends EcosArtifact {

    String SOURCE_ID = "uiserv/config";
    String PATH = "/ui/config/";

    class JSON extends JsonEcosArtifact implements Config {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements Config {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
