package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.DataType;

import java.util.List;

public class CreateDataTypeAction extends AbstractCreateEcosArtifactAction {
    public CreateDataTypeAction() {
        super("Data type", List.of("yaml", "json"), "ecos-data-type", DataType.PATH);
    }
}
