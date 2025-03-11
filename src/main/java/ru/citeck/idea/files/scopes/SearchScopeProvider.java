package ru.citeck.idea.files.scopes;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class SearchScopeProvider implements com.intellij.psi.search.SearchScopeProvider {

    @Override
    public @Nullable String getDisplayName() {
        return "Citeck";
    }

    @Override
    public @NotNull List<com.intellij.psi.search.SearchScope> getSearchScopes(@NotNull Project project, @NotNull DataContext dataContext) {
        return EcosSearchScope.EP_NAME
            .getExtensionsIfPointIsRegistered()
            .stream()
            .map(eScope -> new SearchScope(eScope, project))
            .collect(Collectors.toList());
    }

}
