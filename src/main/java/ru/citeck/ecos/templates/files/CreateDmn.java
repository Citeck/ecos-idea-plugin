package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.process.Dmn;

import java.util.List;

public class CreateDmn extends AbstractCreateEcosArtifactAction {
    public CreateDmn() {
        super("DMN", List.of("dmn.xml"), "ecos-dmn", Dmn.PATH);
    }
}
