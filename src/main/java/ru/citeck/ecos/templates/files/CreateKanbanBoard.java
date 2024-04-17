package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.ui.KanbanBoard;

import java.util.List;

public class CreateKanbanBoard extends AbstractCreateEcosArtifactAction {
    public CreateKanbanBoard() {
        super("Kanban board", List.of("yaml", "json"), "ecos-kanban-board", KanbanBoard.PATH);
    }
}
