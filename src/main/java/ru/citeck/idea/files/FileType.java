package ru.citeck.idea.files;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import ru.citeck.idea.files.types.filters.FileFilter;

public interface FileType {

    FileFilter getFilter();

    default boolean accept(VirtualFile file, Project project) {
        return getFilter().accept(file, project);
    }

    default int getPriority() {
        return 0;
    }
}
