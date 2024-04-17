package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.NumTemplate;

import java.util.List;

public class CreateNumberTemplate extends AbstractCreateEcosArtifactAction {
    public CreateNumberTemplate() {
        super("Number template", List.of("yaml", "json"), "ecos-num-template", NumTemplate.PATH);
    }
}
