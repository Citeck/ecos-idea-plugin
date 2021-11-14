package ru.citeck.deployment

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vfs.VirtualFile

interface FileFetcher {
    fun canFetch(event: AnActionEvent): Boolean
    fun fetch(event: AnActionEvent)
}