package ru.citeck.ecos.files.types.ecos.ui;

import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;

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

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/interface/journals/kanban_board.html";
    }

}
