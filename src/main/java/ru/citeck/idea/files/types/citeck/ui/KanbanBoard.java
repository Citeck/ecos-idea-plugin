package ru.citeck.idea.files.types.citeck.ui;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

public interface KanbanBoard extends EcosArtifact {

    String SOURCE_ID = "uiserv/board";
    String PATH = "/ui/board/";

    class JSON extends JsonEcosArtifact implements KanbanBoard {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements KanbanBoard {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
