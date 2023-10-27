package ru.citeck.ecos.files;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import icons.Icons;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.ServiceRegistry;

import javax.swing.*;

@RequiredArgsConstructor
class SearchScope extends GlobalSearchScope {

    private final FileType fileType;
    private final String searchScopeName;
    private final Project project;

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
        return ServiceRegistry
            .getFileTypeService()
            .isInstance(file, project, fileType.getClass());
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return searchScopeName;
    }

    @Override
    public @Nullable Icon getIcon() {
        return Icons.CiteckLogo;
    }

}
