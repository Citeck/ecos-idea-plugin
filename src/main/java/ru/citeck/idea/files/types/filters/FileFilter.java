package ru.citeck.idea.files.types.filters;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public interface FileFilter {

    boolean accept(VirtualFile file, Project project);

}
