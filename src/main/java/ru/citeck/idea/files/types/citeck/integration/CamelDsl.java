package ru.citeck.idea.files.types.citeck.integration;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

public interface CamelDsl extends EcosArtifact {

    String SOURCE_ID = "integrations/camel-dsl";
    String PATH = "/integration/camel-dsl";

    class JSON extends JsonEcosArtifact implements CamelDsl {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements CamelDsl {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }
}
