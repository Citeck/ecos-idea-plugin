package ru.citeck.ecos.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.NavigateInFileItem;
import ru.citeck.ecos.files.NavigateInFileItemsProvider;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public class NavigateInFileAction extends EcosAction {

    @Override
    protected void perform(@NotNull AnActionEvent event) {

        PsiFile psiFile = getPsiFile(event);
        if (psiFile == null) {
            return;
        }

        Project project = getEventProject(event);

        var items = NavigateInFileItemsProvider.EP_NAME
                .getExtensionsIfPointIsRegistered()
                .stream()
                .map(navigateInFileItemsProvider -> navigateInFileItemsProvider.getItems(psiFile))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(NavigateInFileItem::name))
                .collect(Collectors.toList());

        JBPopupFactory
                .getInstance()
                .createPopupChooserBuilder(items)
                .setTitle("Navigate To:")
                .setItemChosenCallback(chosenItem -> onItemChosen(project, psiFile, chosenItem))
                .setNamerForFiltering(NavigateInFileItem::toString)
                .setRequestFocus(true)
                .createPopup()
                .showInCenterOf(WindowManager.getInstance().getFrame(project).getRootPane());

    }

    private void onItemChosen(Project project, PsiFile psiFile, NavigateInFileItem chosenItem) {
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, psiFile.getVirtualFile());
        Editor editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
        editor.getCaretModel().moveToOffset(chosenItem.psiElement().getTextOffset());
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
    }

    @Override
    boolean canPerform(@NotNull AnActionEvent event) {

        PsiFile psiFile = getPsiFile(event);
        if (psiFile == null) {
            return false;
        }

        return NavigateInFileItemsProvider.EP_NAME
                .getExtensionsIfPointIsRegistered()
                .stream()
                .anyMatch(navigateInFileItemsProvider -> {
                    var items = navigateInFileItemsProvider.getItems(psiFile);
                    return items != null && !items.isEmpty();
                });
    }

}
