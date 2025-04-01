package ru.citeck.idea.artifacts.action.navigate

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiFile
import ru.citeck.idea.actions.CiteckFileAction
import ru.citeck.idea.artifacts.action.navigate.providers.NavigateInFileItem
import ru.citeck.idea.artifacts.action.navigate.providers.NavigateInFileItemsProvider
import java.util.*
import java.util.stream.Collectors

class NavigateInFileAction : CiteckFileAction() {

    override fun perform(event: AnActionEvent) {

        val psiFile = getPsiFile(event) ?: return
        val project = getEventProject(event) ?: return

        val items = NavigateInFileItemsProvider.EP_NAME
            .extensionsIfPointIsRegistered
            .stream()
            .map { provider -> provider.getItems(psiFile) }
            .filter { obj: Collection<NavigateInFileItem>? -> Objects.nonNull(obj) }
            .flatMap { obj: Collection<NavigateInFileItem>? -> obj!!.stream() }
            .sorted(Comparator.comparing(NavigateInFileItem::name))
            .collect(Collectors.toList())

        JBPopupFactory
            .getInstance()
            .createPopupChooserBuilder(items)
            .setTitle("Navigate To:")
            .setItemChosenCallback { chosenItem: NavigateInFileItem ->
                onItemChosen(project, psiFile, chosenItem)
            }
            .setNamerForFiltering { obj: NavigateInFileItem -> obj.toString() }
            .setRequestFocus(true)
            .createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)
    }

    private fun onItemChosen(project: Project, psiFile: PsiFile, chosenItem: NavigateInFileItem) {
        val descriptor = OpenFileDescriptor(project, psiFile.virtualFile)
        val editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true) ?: return
        editor.caretModel.moveToOffset(chosenItem.psiElement.textOffset)
        editor.scrollingModel.scrollToCaret(ScrollType.CENTER_UP)
    }

    override fun isActionAllowed(event: AnActionEvent, file: PsiFile, project: Project): Boolean {

        return NavigateInFileItemsProvider.EP_NAME
            .extensionsIfPointIsRegistered
            .stream()
            .anyMatch { navigateInFileItemsProvider: NavigateInFileItemsProvider ->
                val items = navigateInFileItemsProvider.getItems(file)
                !items.isNullOrEmpty()
            }
    }
}
