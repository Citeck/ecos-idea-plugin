package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.XmlNamespaceFilter;

public class CmmnProcessDefinition implements FileType {

    public static final String NAMESPACE = "http://www.omg.org/spec/CMMN/20151109/MODEL";

    private final FileFilter filter = new XmlNamespaceFilter(NAMESPACE);

    @Override
    public FileFilter getFilter() {
        return filter;
    }

}
