package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.ui.Menu;

import java.util.List;

public class CreateMenu extends AbstractCreateEcosArtifactAction {
    public CreateMenu() {
        super("Menu", List.of("json"), "ecos-menu", Menu.PATH);
    }
}
