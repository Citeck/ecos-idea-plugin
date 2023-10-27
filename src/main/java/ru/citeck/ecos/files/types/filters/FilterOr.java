package ru.citeck.ecos.files.types.filters;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Arrays;

public class FilterOr implements FileFilter {

    private final FileFilter[] fileFilters;

    public FilterOr(FileFilter... fileFilters) {
        this.fileFilters = fileFilters;
    }

    @Override
    public boolean accept(VirtualFile file, Project project) {
        return Arrays.stream(fileFilters).anyMatch(fileFilter -> fileFilter.accept(file, project));
    }
}
