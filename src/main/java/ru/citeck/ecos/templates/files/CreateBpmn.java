package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.Bpmn;

import java.util.List;

public class CreateBpmn extends AbstractCreateEcosArtifactAction {
    public CreateBpmn() {
        super("BPMN", List.of("bpmn.xml"), "ecos-bpmn", Bpmn.PATH);
    }
}
