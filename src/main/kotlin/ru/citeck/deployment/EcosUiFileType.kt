package ru.citeck.deployment

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile

enum class EcosUiFileType(
    val typeName: String,
    val directory: String
) {

    EFORM("eform", "/ecos-forms"),
    DASHBOARD("dashboard", "/ui/dashboard"),
    JOURNAL("journal", "/ui/journal"),
    ACTION("action", "/ui/action"),
    MENU("menu", "/ui/menu");

    companion object {
        fun get(event: AnActionEvent): EcosUiFileType? {
            val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return null
            values().forEach { type -> if (type.accept(virtualFile)) return type }
            return null
        }
    }

    fun accept(virtualFile: VirtualFile): Boolean {
        return virtualFile.extension == "json" &&
                virtualFile.parent.path.endsWith(directory)
    }

    override fun toString(): String {
        return typeName
    }
}