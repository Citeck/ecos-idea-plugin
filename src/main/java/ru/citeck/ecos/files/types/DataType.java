package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

import java.util.Collections;
import java.util.List;

@SearchScopeName("Data type")
public class DataType extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "emodel/types-repo";

    public DataType() {
        super("/model/type/");
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

    @Override
    public List<String> getAdditionalReferences(String artifactId) {
        return Collections.singletonList("emodel/type@" + artifactId);
    }

}
