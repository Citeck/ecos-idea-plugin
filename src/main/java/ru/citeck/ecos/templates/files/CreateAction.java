package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.ui.Action;

import java.util.List;

public class CreateAction extends AbstractCreateEcosArtifactAction {
    public CreateAction() {
        super("Action", List.of("yaml", "json"), "ecos-action", Action.PATH);
    }
}
