package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.integration.CamelDsl;

import java.util.List;

public class CreateCamelDsl extends AbstractCreateEcosArtifactAction {
    public CreateCamelDsl() {
        super("Camel DSL", List.of("yaml", "json"), "ecos-camel-dsl", CamelDsl.PATH);
    }
}
