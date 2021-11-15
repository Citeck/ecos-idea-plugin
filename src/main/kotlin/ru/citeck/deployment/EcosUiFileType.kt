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
            EcosUiFileType("journal", "ui/journal")
        )

        fun get(event: AnActionEvent): EcosUiFileType? {
            val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return null
            val fileDirectory = virtualFile.parent.path
            TYPES.forEach { type ->
                if (fileDirectory.endsWith(type.directory)) {
                    return type
                }
            }
            return null
        }

    }

}