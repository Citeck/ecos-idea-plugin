package ru.citeck.idea.files.types.filters;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Arrays;

public class FolderNamePatternsFilter implements FileFilter {

    private final String[] patterns;

    public FolderNamePatternsFilter(String... patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean accept(VirtualFile file, Project project) {
        String path = file.getPath();
        return Arrays.stream(patterns).anyMatch(path::contains);
    }
}
