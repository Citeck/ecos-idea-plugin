package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;

public class PermissionSettings extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "emodel/permission-settings";
    public static final String PATH = "/model/permission-settings";

    public PermissionSettings() {
        super(PATH);
    }
    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
