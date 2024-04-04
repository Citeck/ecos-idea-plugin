package ru.citeck.ecos.files.types.ecos;

public interface Menu extends EcosArtifact {

    String SOURCE_ID = "uiserv/menu";
    String PATH = "/ui/menu/";

    class JSON extends JsonEcosArtifact implements Menu {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements Menu {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
