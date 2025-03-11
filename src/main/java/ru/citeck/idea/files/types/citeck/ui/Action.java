package ru.citeck.idea.files.types.citeck.ui;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

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

}
