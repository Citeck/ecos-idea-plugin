package ru.citeck.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.indexing.FileBasedIndex
import ru.citeck.deployment.EcosUiFileType
import ru.citeck.indexes.EcosUiFileBasedIndex
import javax.swing.ListSelectionModel

class EcosArtifactsNavigator : AnAction() {


    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return

        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(EcosUiFileType.values().sortedBy { it.typeName })
            .setTitle("Ecos Artifacts Navigator:")
            .setItemChosenCallback { showArtifacts(project, it) }

            .setNamerForFiltering { it.typeName }
            .setRequestFocus(true).createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)

    }

    private class FileWrapper(val file: VirtualFile, val id: String) {
        override fun toString(): String {
            return id
        }
    }

    private fun showArtifacts(project: Project, fileType: EcosUiFileType) {

        val fbi = FileBasedIndex.getInstance()
        val files = fbi.getContainingFiles(EcosUiFileBasedIndex.NAME, fileType, GlobalSearchScope.projectScope(project))
            .map { FileWrapper(it, fbi.getFileData(EcosUiFileBasedIndex.NAME, it, project)[fileType]!!) }
            .sortedBy { it.id }

        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(files)
            .setTitle("Navigate to ${fileType.typeName}:")
            .setItemsChosenCallback { choosenItems ->
                choosenItems.forEach {
                    FileEditorManager.getInstance(project).openFile(it.file, true)
                }
            }
            .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
            .setNamerForFiltering { it.id }
            .setRequestFocus(true).createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)

    }

}