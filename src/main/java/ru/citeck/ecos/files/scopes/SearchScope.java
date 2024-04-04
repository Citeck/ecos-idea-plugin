package ru.citeck.ecos.files.scopes;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import icons.Icons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileType;

import javax.swing.*;

class SearchScope extends GlobalSearchScope {

    private final Class<? extends FileType> fileTypeClass;
    private final String displayName;
    private final Project project;

    public SearchScope(EcosSearchScope ecosSearchScope, @NotNull Project project) {
        this.project = project;
        this.displayName = ecosSearchScope.name;

        Class<? extends FileType> fileTypeClass;
        try {
            //noinspection unchecked
            fileTypeClass = (Class<? extends FileType>) Class.forName(ecosSearchScope.clazz);
        } catch (ClassNotFoundException e) {
            fileTypeClass = null;
        }
        this.fileTypeClass = fileTypeClass;
    }

    @Override
    public boolean isSearchInModuleContent(@NotNull Module aModule) {
        return true;
    }

    @Override
    public boolean isSearchInLibraries() {
        return true;
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
        if (fileTypeClass == null) {
            return false;
        }
        return ServiceRegistry
                .getFileTypeService()
                .isInstance(file, project, fileTypeClass);
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return displayName;
    }

    @Override
    public @Nullable Icon getIcon() {
        return Icons.CiteckLogo;
    }

}
