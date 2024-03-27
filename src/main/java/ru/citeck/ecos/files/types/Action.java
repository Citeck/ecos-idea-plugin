package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

@SearchScopeName("Action")
public class Action extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "uiserv/action";

    public Action() {
        super("/ui/action/");
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
