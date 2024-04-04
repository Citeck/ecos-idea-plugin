package ru.citeck.ecos.files.types.ecos;

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
