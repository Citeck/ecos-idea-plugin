package ru.citeck.ecos.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileType;

public abstract class EcosAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        event.getPresentation().setVisible(canPerform(event));
    }

    protected abstract void perform(@NotNull AnActionEvent event);

    abstract boolean canPerform(@NotNull AnActionEvent event);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        if (!canPerform(event)) {
            return;
        }
        perform(event);
    }

    public PsiFile getPsiFile(AnActionEvent event) {
        return event.getData(PlatformDataKeys.PSI_FILE);
    }

    public FileType resolveFileType(AnActionEvent event) {
        return ServiceRegistry.getFileTypeService().getFileType(getPsiFile(event));
    }

    public void runUndoTransparentAction(Runnable action) {
        ApplicationManager.getApplication().runWriteAction(() ->
            CommandProcessor.getInstance().runUndoTransparentAction(action)
        );
    }

}
