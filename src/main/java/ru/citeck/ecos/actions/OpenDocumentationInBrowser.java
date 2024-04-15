package ru.citeck.ecos.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.HasDocumentation;

import java.util.Optional;

public class OpenDocumentationInBrowser extends EcosAction {
    @Override
    protected void perform(@NotNull AnActionEvent event) {
        PsiFile psiFile = getPsiFile(event);
        if (psiFile == null) {
            return;
        }
        getDocumentationUrl(event)
                .ifPresent(BrowserUtil::browse);
    }

    @Override
    boolean canPerform(@NotNull AnActionEvent event) {
        return getDocumentationUrl(event).isPresent();
    }

    private Optional<String> getDocumentationUrl(AnActionEvent event) {
        return Optional
                .ofNullable(resolveFileType(event))
                .filter(HasDocumentation.class::isInstance)
                .map(HasDocumentation.class::cast)
                .map(HasDocumentation::getDocumentationUrl);
    }

}
