package ru.citeck.idea.search.scope

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.SearchScopeProvider
import icons.Icons
import org.jetbrains.annotations.Nls
import ru.citeck.idea.artifacts.ArtifactTypes
import ru.citeck.idea.artifacts.ArtifactsService
import ru.citeck.idea.project.CiteckProject
import javax.swing.Icon

class SearchScopeProvider : SearchScopeProvider {

    override fun getDisplayName(): String {
        return "Citeck"
    }

    override fun getSearchScopes(
        project: Project,
        dataContext: DataContext
    ): List<com.intellij.psi.search.SearchScope> {
        return listOf(
            CiteckSearchScope(project, "Artifacts"),
            CiteckSearchScope(project, "Data Types", setOf(ArtifactTypes.TYPE_TYPE)),
            CiteckSearchScope(project, "Journals", setOf(ArtifactTypes.TYPE_JOURNAL)),
            CiteckSearchScope(project, "Forms", setOf(ArtifactTypes.TYPE_FORM))
        )
    }

    private class CiteckSearchScope(
        private val project: Project,
        @Nls
        private val name: String,
        private val artifactTypes: Set<String> = emptySet()
    ) : GlobalSearchScope(project) {

        override fun isSearchInModuleContent(aModule: Module): Boolean {
            return CiteckProject.getInstance(project).isCiteckModule(aModule)
        }

        override fun isSearchInLibraries(): Boolean {
            return false
        }

        override fun contains(file: VirtualFile): Boolean {
            val meta = ArtifactsService.getInstance().getArtifactTypeMeta(
                PsiManager.getInstance(project).findFile(file)
            ) ?: return false
            return artifactTypes.isEmpty() || artifactTypes.contains(meta.typeId)
        }

        @Nls
        override fun getDisplayName(): String {
            return name
        }

        override fun getIcon(): Icon {
            return Icons.CiteckLogo
        }
    }
}
