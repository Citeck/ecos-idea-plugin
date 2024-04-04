package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.Bpmn;

import java.util.List;

public class CreateBpmnAction extends AbstractCreateEcosArtifactAction {
    public CreateBpmnAction() {
        super("BPMN", List.of("bpmn.xml"), "ecos-bpmn", Bpmn.PATH);
    }
}
