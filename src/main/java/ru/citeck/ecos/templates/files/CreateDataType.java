package ru.citeck.ecos.templates.files;

import ru.citeck.ecos.files.types.ecos.model.DataType;

import java.util.List;

public class CreateDataType extends AbstractCreateEcosArtifactAction {
    public CreateDataType() {
        super("Data type", List.of("yaml", "json"), "ecos-data-type", DataType.PATH);
    }
}
