package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.EcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.FilterAnd;
import ru.citeck.ecos.files.types.filters.FolderNamePatternsFilter;

@SearchScopeName("Action")
public class Action implements EcosArtifact {

    public static final String SOURCE_ID = "uiserv/action";

    private final FilterAnd filter = new FilterAnd(
        FileExtensionFilter.JSON,
        new FolderNamePatternsFilter("/ui/action/")
    );

    @Override
    public FileFilter getFilter() {
        return filter;
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }
}
