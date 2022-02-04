package ru.citeck.search

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.SearchScopeProvider
import ru.citeck.deployment.EcosUiFileType

class EcosArtifactsSearchScopeProvider : SearchScopeProvider {

    override fun getDisplayName(): String {
        return "Ecos Artifacts"
    }

    override fun getSearchScopes(project: Project, dataContext: DataContext): MutableList<SearchScope> {
        return EcosUiFileType.values().map { EcosUiSearchScope(project, it) }
            .union(listOf(AlfrescoModelSearchScope(project)))
            .toMutableList()
    }

    override fun getGeneralSearchScopes(project: Project, dataContext: DataContext): MutableList<SearchScope> {
        return mutableListOf()
    }

}

