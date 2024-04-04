package ru.citeck.ecos.files.types.ecos;

public interface Dashboard extends EcosArtifact {

    String SOURCE_ID = "uiserv/dashboard";
    String PATH = "/ui/dashboard/";

    class JSON extends JsonEcosArtifact implements Dashboard {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements Dashboard {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
