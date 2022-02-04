package ru.citeck.search

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import icons.EcosIcons
import ru.citeck.deployment.EcosUiFileType
import javax.swing.Icon

class EcosUiSearchScope(project: Project, private val ecosUiFileType: EcosUiFileType): GlobalSearchScope(project) {

    override fun getDisplayName(): String {
        return ecosUiFileType.typeName
    }

    override fun getIcon(): Icon {
        return EcosIcons.CiteckLogo
    }

    override fun contains(file: VirtualFile): Boolean {
        return ecosUiFileType.accept(file)
    }

    override fun isSearchInModuleContent(aModule: Module): Boolean {
        return true
    }

    override fun isSearchInLibraries(): Boolean {
        return true
    }


}