package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.EcosConfiguration;

import java.util.List;

public class CreateEcosConfiguration extends AbstractCreateEcosArtifactAction {
    public CreateEcosConfiguration() {
        super("ECOS Configuration", List.of("yaml", "json"), "ecos-ecos-configuration", EcosConfiguration.PATH);
    }
}
