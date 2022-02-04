package ru.citeck.actions

import com.intellij.ide.scratch.ScratchUtil
import com.intellij.ide.scratch.ScratchesSearchScope
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.ui.awt.RelativePoint

class ExecuteAlfrescoJsFromScratchFile : AnAction() {

    private class JsFile(val virtualFile: VirtualFile, val project: Project) {
        override fun toString(): String {
            var path = ScratchUtil.getRelativePath(project, virtualFile)
            if (path.startsWith("Scratches")) {
                path = path.substring("Scratches".length)
            }
            return path
        }
    }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(
                FilenameIndex.getAllFilesByExt(
                    project,
                    "js",
                    ScratchesSearchScope.getScratchesScope(event.project!!)
                ).map { JsFile(it, project) }
            )
            .setTitle("Execute Alfresco JS:")
            .setItemChosenCallback {
                val executeAlfrescoJSAction =
                    ActionManager.getInstance().getAction("Ecos.ExecuteAlfrescoJs") as ExecuteAlfrescoJS
                executeAlfrescoJSAction.executeJs(project, it.virtualFile.inputStream.bufferedReader().readText())
            }
            .setNamerForFiltering { it.toString() }
            .setRequestFocus(true).createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)
    }

}