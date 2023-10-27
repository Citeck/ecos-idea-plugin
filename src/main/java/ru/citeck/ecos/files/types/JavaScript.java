package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.files.types.filters.FileFilter;

public class JavaScript implements FileType {

    @Override
    public FileFilter getFilter() {
        return FileExtensionFilter.JS;
    }

}
