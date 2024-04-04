package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.NumTemplate;

import java.util.List;

public class CreateNumberTemplateAction extends AbstractCreateEcosArtifactAction {
    public CreateNumberTemplateAction() {
        super("Number template", List.of("yaml", "json"), "ecos-num-template", NumTemplate.PATH);
    }
}