package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.process.Cmmn;

import java.util.List;

public class CreateCmmn extends AbstractCreateEcosArtifactAction {
    public CreateCmmn() {
        super("CMMN", List.of("xml"), "ecos-cmmn", Cmmn.PATH);
    }
}
