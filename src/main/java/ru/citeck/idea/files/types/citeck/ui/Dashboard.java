package ru.citeck.idea.files.types.citeck.ui;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

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
