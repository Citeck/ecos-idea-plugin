package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

@SearchScopeName("Kanban board")
public class KanbanBoard extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "uiserv/board";
    public static final String PATH = "/ui/board/";

    public KanbanBoard() {
        super(PATH);
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
