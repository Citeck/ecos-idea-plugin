package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.SearchScopeName;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.XmlNamespaceFilter;

@SearchScopeName("Alfresco model")
public class AlfrescoModel implements FileType {

    public static final String NAMESPACE = "http://www.alfresco.org/model/dictionary/1.0";

    private final FileFilter filter = new XmlNamespaceFilter(NAMESPACE);

    @Override
    public FileFilter getFilter() {
        return filter;
    }

}
