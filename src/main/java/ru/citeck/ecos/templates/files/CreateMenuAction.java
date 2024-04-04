package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.Dashboard;
import ru.citeck.ecos.files.types.Menu;

import java.util.List;

public class CreateMenuAction extends AbstractCreateEcosArtifactAction {
    public CreateMenuAction() {
        super("Menu", List.of("json"), "ecos-menu", Menu.PATH);
    }
}
