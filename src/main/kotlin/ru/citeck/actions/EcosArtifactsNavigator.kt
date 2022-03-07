package ru.citeck.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import ru.citeck.deployment.EcosUiFileType
import ru.citeck.indexes.EcosUiFileBasedIndex
import ru.citeck.indexes.models.AlfTypeIndex
import javax.swing.ListSelectionModel


class EcosArtifactsNavigator : AnAction() {


    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return

        val items = mutableListOf<Any>()
        items.addAll(EcosUiFileType.values().sortedBy { it.typeName })
        items.add("alf_type")

        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(items)
            .setTitle("Ecos Artifacts Navigator:")
            .setItemChosenCallback { showArtifacts(project, it) }
            .setNamerForFiltering {
                if (it is EcosUiFileType) {
                    it.typeName
                } else {
                    it.toString()
                }
            }
            .setRequestFocus(true).createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)

    }

    private class FileWrapper(val file: VirtualFile, val id: String) {
        override fun toString(): String {
            return id
        }
    }

    private fun showArtifacts(project: Project, artifactType: Any) {
        if (artifactType is EcosUiFileType) {
            showEcosUiArtifacts(project, artifactType)
        } else if (artifactType == "alf_type") {
            showAlfrescoTypes(project)
        }
    }

    private fun showEcosUiArtifacts(project: Project, artifactType: EcosUiFileType) {
        val fbi = FileBasedIndex.getInstance()
        val files =
            fbi.getContainingFiles(EcosUiFileBasedIndex.NAME, artifactType, GlobalSearchScope.projectScope(project))
                .map { FileWrapper(it, fbi.getFileData(EcosUiFileBasedIndex.NAME, it, project)[artifactType]!!) }
                .sortedBy { it.id }

        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(files)
            .setTitle("Navigate to ${artifactType.typeName}:")
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

    private fun showAlfrescoTypes(project: Project) {
        val fbi = FileBasedIndex.getInstance()
        val files = fbi.getAllKeys(AlfTypeIndex.NAME, project).sortedBy { it }.toMutableList()

        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(files)
            .setTitle("Navigate to alfresco type:")
            .setItemsChosenCallback { choosenItems ->
                val searchScope = GlobalSearchScope.allScope(project)
                choosenItems.forEach { model ->
                    fbi.getContainingFiles(AlfTypeIndex.NAME, model, searchScope).forEach { file ->
                        val descriptor = OpenFileDescriptor(project, file)
                        val editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true)
                        val offset = fbi.getValues(AlfTypeIndex.NAME, model, searchScope).first().psiElementOffset
                        editor?.caretModel?.moveToOffset(offset, true)
                        editor?.scrollingModel?.scrollToCaret(ScrollType.CENTER_UP)
                    }
                }
            }
            .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
            .setNamerForFiltering { it }
            .setRequestFocus(true).createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)
    }

}