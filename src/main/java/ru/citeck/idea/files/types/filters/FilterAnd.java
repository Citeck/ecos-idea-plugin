package ru.citeck.idea.files.types.filters;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Arrays;

public class FilterAnd implements FileFilter {

    private final FileFilter[] fileFilters;

    public FilterAnd(FileFilter... fileFilters) {
        this.fileFilters = fileFilters;
    }

    @Override
    public boolean accept(VirtualFile file, Project project) {
        return Arrays.stream(fileFilters).allMatch(fileFilter -> fileFilter.accept(file, project));
    }
}
