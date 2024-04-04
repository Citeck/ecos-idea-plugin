package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.Form;
import ru.citeck.ecos.files.types.KanbanBoard;

import java.util.List;

public class CreateKanbanBoardAction extends AbstractCreateEcosArtifactAction {
    public CreateKanbanBoardAction() {
        super("Kanban board", List.of("yaml", "json"), "ecos-kanban-board", KanbanBoard.PATH);
    }
}
