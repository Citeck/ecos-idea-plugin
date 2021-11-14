package ru.citeck.deployment

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vfs.VirtualFile

interface FileDeployer {
    fun canDeploy(event: AnActionEvent): Boolean
    fun deploy(event: AnActionEvent)
}