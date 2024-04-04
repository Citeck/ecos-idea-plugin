package ru.citeck.ecos.files.types.ecos;

public interface PermissionSettings extends EcosArtifact {

    String SOURCE_ID = "emodel/permission-settings";
    String PATH = "/model/permission-settings";

    class JSON extends JsonEcosArtifact implements PermissionSettings {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements PermissionSettings {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
