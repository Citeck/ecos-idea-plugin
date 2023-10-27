package ru.citeck.ecos.files.types.filters;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class FileExtensionFilter implements FileFilter {

    public static final FileFilter JS = new FileExtensionFilter("js");
    public static final FileFilter JSON = new FileExtensionFilter("json");
    public static final FileFilter XML = new FileExtensionFilter("xml");
    public static final FileFilter FTL = new FileExtensionFilter("ftl");
    public static final FileFilter JAVA = new FileExtensionFilter("java");


    private final String extension;

    public FileExtensionFilter(String extension) {
        this.extension = extension.toLowerCase();
    }

    @Override
    public boolean accept(VirtualFile file, Project project) {
        String extension = file.getExtension();
        if (extension == null) {
            return false;
        }
        return this.extension.equals(extension.toLowerCase());
    }
}
