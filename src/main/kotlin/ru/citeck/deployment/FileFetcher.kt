package ru.citeck.deployment

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.Icon

interface FileFetcher {
    fun canFetch(event: AnActionEvent): Boolean
    fun fetch(event: AnActionEvent)
    val icon: Icon get() = AllIcons.Actions.Install
}