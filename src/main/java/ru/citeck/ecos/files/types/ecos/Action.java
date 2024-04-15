package ru.citeck.ecos.files.types.ecos;

public interface Action extends EcosArtifact {

    String SOURCE_ID = "uiserv/action";
    String PATH = "/ui/action/";

    class JSON extends JsonEcosArtifact implements Action {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements Action {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/ui_actions.html";
    }

}
