package ru.citeck.idea.files.types.filters;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileExtensionFilter implements FileFilter {

    public static final FileFilter JS = new FileExtensionFilter("js");
    public static final FileFilter JSON = new FileExtensionFilter("json");
    public static final FileFilter XML = new FileExtensionFilter("xml");
    public static final FileFilter FTL = new FileExtensionFilter("ftl");
    public static final FileFilter JAVA = new FileExtensionFilter("java");
    public static final FileFilter YAML = new FilterOr(
        new FileExtensionFilter("yml"),
        new FileExtensionFilter("yaml")
    );

    private final String extension;

    @Override
    public boolean accept(VirtualFile file, Project project) {
        String extension = file.getExtension();
        if (extension == null) {
            return false;
        }
        return this.extension.equalsIgnoreCase(extension);
    }
}
