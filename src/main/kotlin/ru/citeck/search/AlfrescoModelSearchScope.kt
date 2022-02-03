package ru.citeck.search

import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import icons.EcosIcons
import javax.swing.Icon

class AlfrescoModelSearchScope(project: Project): GlobalSearchScope(project) {

    override fun getDisplayName(): String {
        return "model"
    }

    override fun getIcon(): Icon {
        return EcosIcons.CiteckLogo
    }

    override fun contains(file: VirtualFile): Boolean {
        return file.extension == "xml" &&
                file.parent?.path?.endsWith("/model") ?: false
    }

    override fun isSearchInModuleContent(aModule: Module): Boolean {
        return true
    }

    override fun isSearchInLibraries(): Boolean {
        return true
    }

}