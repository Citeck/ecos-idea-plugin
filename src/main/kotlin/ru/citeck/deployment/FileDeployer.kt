package ru.citeck.deployment

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.Icon

interface FileDeployer {
    fun canDeploy(event: AnActionEvent): Boolean
    fun deploy(event: AnActionEvent)
    val icon: Icon get() = AllIcons.Actions.Uninstall
}