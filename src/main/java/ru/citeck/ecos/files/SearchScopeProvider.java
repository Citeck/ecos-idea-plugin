package ru.citeck.ecos.files;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.ServiceRegistry;

import java.util.List;
import java.util.stream.Collectors;

public class SearchScopeProvider implements com.intellij.psi.search.SearchScopeProvider {

    @Override
    public @Nullable String getDisplayName() {
        return "ECOS";
    }

    @Override
    public @NotNull List<com.intellij.psi.search.SearchScope> getSearchScopes(@NotNull Project project, @NotNull DataContext dataContext) {
        return ServiceRegistry
            .getFileTypeService()
            .getRegisteredFileTypes()
            .stream()
            .filter(fileType -> fileType.getClass().isAnnotationPresent(SearchScopeName.class))
            .map(fileType -> new SearchScope(
                fileType,
                fileType.getClass().getAnnotationsByType(SearchScopeName.class)[0].value(),
                project)
            )
            .collect(Collectors.toList());
    }

}