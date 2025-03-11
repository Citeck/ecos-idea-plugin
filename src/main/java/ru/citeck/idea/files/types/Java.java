package ru.citeck.idea.files.types;

import ru.citeck.idea.files.FileType;
import ru.citeck.idea.files.types.filters.FileExtensionFilter;
import ru.citeck.idea.files.types.filters.FileFilter;

public class Java implements FileType {

    @Override
    public FileFilter getFilter() {
        return FileExtensionFilter.JAVA;
    }
}
