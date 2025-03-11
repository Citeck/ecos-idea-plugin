package ru.citeck.idea.files.types.citeck.model;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

public interface NumTemplate extends EcosArtifact {

    String SOURCE_ID = "emodel/num-template";
    String PATH = "/model/num-template/";

    class JSON extends JsonEcosArtifact implements NumTemplate {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements NumTemplate {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
