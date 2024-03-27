package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

@SearchScopeName("Number template")
public class NumTemplate extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "emodel/num-template";

    public NumTemplate() {
        super("/model/num-template/");
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
