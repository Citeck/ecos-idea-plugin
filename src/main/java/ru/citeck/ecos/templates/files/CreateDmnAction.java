package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.Dmn;

import java.util.List;

public class CreateDmnAction extends AbstractCreateEcosArtifactAction {
    public CreateDmnAction() {
        super("DMN", List.of("dmn.xml"), "ecos-dmn", Dmn.PATH);
    }
}
