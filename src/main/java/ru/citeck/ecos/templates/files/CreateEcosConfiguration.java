package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.app.EcosConfiguration;

import java.util.List;

public class CreateEcosConfiguration extends AbstractCreateEcosArtifactAction {
    public CreateEcosConfiguration() {
        super("ECOS Configuration", List.of("yml", "json"), "ecos-ecos-configuration", EcosConfiguration.PATH);
    }
}
