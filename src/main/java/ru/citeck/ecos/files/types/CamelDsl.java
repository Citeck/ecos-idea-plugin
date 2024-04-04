package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;

public class CamelDsl extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "integrations/camel-dsl";
    public static final String PATH = "/integration/camel-dsl";

    public CamelDsl() {
        super(PATH);
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }
}
