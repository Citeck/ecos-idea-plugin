package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.EcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.FilterAnd;
import ru.citeck.ecos.files.types.filters.FolderNamePatternsFilter;

import java.util.Collections;
import java.util.List;

@SearchScopeName("Data type")
public class DataType implements EcosArtifact {

    public static final String SOURCE_ID = "emodel/types-repo";

    private final FilterAnd filter = new FilterAnd(
        FileExtensionFilter.JSON,
        new FolderNamePatternsFilter("/model/type/")
    );

    @Override
    public FileFilter getFilter() {
        return filter;
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
