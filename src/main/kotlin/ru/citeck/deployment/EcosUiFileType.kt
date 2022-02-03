package ru.citeck.deployment

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile

class EcosUiFileType(
    val name: String,
    val directory: String
) {

    companion object {

        val TYPES = listOf(
            EcosUiFileType("eform", "/ecos-forms"),
            EcosUiFileType("dashboard", "/ui/dashboard"),
            EcosUiFileType("journal", "/ui/journal"),
            EcosUiFileType("action", "/ui/action"),
            EcosUiFileType("menu", "/ui/menu")
        )

        fun get(event: AnActionEvent): EcosUiFileType? {
            val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return null
            TYPES.forEach { type -> if (type.accept(virtualFile)) return type }
            return null
        }

    }

    fun accept(virtualFile: VirtualFile): Boolean {
        return virtualFile.extension == "json" &&
                virtualFile.parent.path.endsWith(directory)
    }

}