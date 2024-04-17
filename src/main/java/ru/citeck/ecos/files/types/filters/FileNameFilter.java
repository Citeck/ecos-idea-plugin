package ru.citeck.ecos.files.types.filters;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileNameFilter implements FileFilter {

    private final String fileName;

    @Override
    public boolean accept(VirtualFile file, Project project) {
        return file.getPath().endsWith(fileName);
    }

}
