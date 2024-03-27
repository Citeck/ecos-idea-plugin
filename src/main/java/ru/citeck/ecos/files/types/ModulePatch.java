package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.SearchScopeName;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.FilterAnd;
import ru.citeck.ecos.files.types.filters.FolderNamePatternsFilter;

@SearchScopeName("Module patch")
public class ModulePatch implements BrowsableEcosArtifact {

    public static final String SOURCE_ID = "eapps/artifact-patch";

    private final FilterAnd filter = new FilterAnd(
        FileExtensionFilter.JSON,
        new FolderNamePatternsFilter("/app/module-patch/")
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
