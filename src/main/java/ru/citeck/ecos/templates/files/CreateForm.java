package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.ui.Form;

import java.util.List;

public class CreateForm extends AbstractCreateEcosArtifactAction {
    public CreateForm() {
        super("Form", List.of("yml", "json"), "ecos-form", Form.PATH);
    }
}
