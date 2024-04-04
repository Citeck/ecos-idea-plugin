package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;

public class Permissions extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "emodel/perms";
    public static final String PATH = "/model/permissions";

    public Permissions() {
        super(PATH);
    }
    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
