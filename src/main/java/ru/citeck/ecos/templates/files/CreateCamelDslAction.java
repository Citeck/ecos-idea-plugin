package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.CamelDsl;

import java.util.List;

public class CreateCamelDslAction extends AbstractCreateEcosArtifactAction {
    public CreateCamelDslAction() {
        super("Camel DSL", List.of("yaml", "json"), "ecos-camel-dsl", CamelDsl.PATH);
    }
}
