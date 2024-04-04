package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.Form;

import java.util.List;

public class CreateFormAction extends AbstractCreateEcosArtifactAction {
    public CreateFormAction() {
        super("Form", List.of("yaml", "json"), "ecos-form", Form.PATH);
    }
}
