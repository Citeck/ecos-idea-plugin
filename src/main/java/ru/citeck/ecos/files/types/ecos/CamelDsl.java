package ru.citeck.ecos.files.types.ecos;

public class CamelDsl {

    public static final String SOURCE_ID = "integrations/camel-dsl";
    public static final String PATH = "/integration/camel-dsl";

    public static class JSON extends JsonEcosArtifact {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    public static class YAML extends YamlEcosArtifact {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
