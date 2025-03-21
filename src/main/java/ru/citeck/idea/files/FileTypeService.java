package ru.citeck.idea.files;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import lombok.Getter;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class FileTypeService {

    public static final ExtensionPointName<FileType> EP_NAME =
        ExtensionPointName.create("ru.citeck.idea.fileType");

    public static FileTypeService getInstance() {
        return ApplicationManager.getApplication().getService(FileTypeService.class);
    }

    @Getter(lazy = true)
    private final Map<Class<? extends FileType>, FileType> fileTypesByClass = initFileTypesByName();

    private Map<Class<? extends FileType>, FileType> initFileTypesByName() {
        return EP_NAME
            .getExtensionsIfPointIsRegistered()
            .stream()
            .collect(Collectors.toMap(FileType::getClass, fileType -> fileType));
    }

    public FileType getFileType(VirtualFile file, Project project) {
        if (file == null || project == null) {
            return null;
        }
        return EP_NAME
            .getExtensionsIfPointIsRegistered()
            .stream()
            .filter(fileType -> fileType.accept(file, project))
            .max(Comparator.comparingInt(FileType::getPriority))
            .orElse(null);
    }

    public FileType getFileType(PsiFile psiFile) {
        if (psiFile == null) {
            return null;
        }
        return getFileType(psiFile.getVirtualFile(), psiFile.getProject());
    }

    public boolean isInstance(VirtualFile file, Project project, Class<? extends FileType> clazz) {
        FileType fileType = getFileType(file, project);
        if (fileType == null) {
            return false;
        }
        return clazz.isInstance(fileType);
    }

    public boolean isInstance(PsiFile psiFile, Class<? extends FileType> clazz) {
        return isInstance(psiFile.getVirtualFile(), psiFile.getProject(), clazz);
    }

}
