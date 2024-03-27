package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

@SearchScopeName("Config")
public class Config extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "uiserv/config";

    public Config() {
        super("/ui/config/");
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
