package ru.citeck.idea.files.types.citeck.ui;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

public interface Menu extends EcosArtifact {

    String SOURCE_ID = "uiserv/menu";
    String PATH = "/ui/menu/";

    class JSON extends JsonEcosArtifact implements Menu {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements Menu {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
