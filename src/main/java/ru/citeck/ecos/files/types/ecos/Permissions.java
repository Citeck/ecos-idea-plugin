package ru.citeck.ecos.files.types.ecos;

public interface Permissions extends EcosArtifact {

    String SOURCE_ID = "emodel/perms";
    String PATH = "/model/permissions";

    class JSON extends JsonEcosArtifact implements Permissions {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements Permissions {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
