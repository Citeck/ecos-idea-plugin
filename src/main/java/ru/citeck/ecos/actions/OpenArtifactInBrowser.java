package ru.citeck.ecos.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.BrowsableArtifact;

import java.util.Optional;

public class OpenArtifactInBrowser extends EcosAction {
    @Override
    protected void perform(@NotNull AnActionEvent event) {
        PsiFile psiFile = getPsiFile(event);
        if (psiFile == null) {
            return;
        }
        Optional
                .ofNullable(resolveFileType(event))
                .filter(BrowsableArtifact.class::isInstance)
                .map(BrowsableArtifact.class::cast)
                .ifPresent(browsableArtifact -> BrowserUtil.browse(browsableArtifact.getURL(psiFile)));
    }

    @Override
    boolean canPerform(@NotNull AnActionEvent event) {
        return resolveFileType(event) instanceof BrowsableArtifact;
    }


}