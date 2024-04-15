package ru.citeck.ecos.files.types.ecos;

public interface CamelDsl extends EcosArtifact {

    String SOURCE_ID = "integrations/camel-dsl";
    String PATH = "/integration/camel-dsl";

    public static class JSON extends JsonEcosArtifact implements CamelDsl {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    public static class YAML extends YamlEcosArtifact implements CamelDsl {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/integration/Camel_DSL.html";
    }

}
