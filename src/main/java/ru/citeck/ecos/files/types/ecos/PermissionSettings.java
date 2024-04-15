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

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/%D0%A2%D0%B8%D0%BF%D1%8B_%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D1%85.html#data-type-rights";
    }


}
