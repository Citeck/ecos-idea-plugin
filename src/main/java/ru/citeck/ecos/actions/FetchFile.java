package ru.citeck.ecos.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.FileFetcher;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.settings.EcosServer;
import ru.citeck.ecos.utils.EcosMessages;

public class FetchFile extends EcosAction {

    @Override
    protected void perform(@NotNull AnActionEvent event) {

        Project project = event.getProject();
        if (project == null) {
            return;
        }

        FileFetcher fetcher = getFetcher(event);
        if (fetcher == null) {
            return;
        }

        PsiFile psiFile = getPsiFile(event);
        if (psiFile == null) {
            return;
        }

        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        EcosServer.doWithServer(project, ecosServer -> {

            String sourceName = fetcher.getSourceName(ecosServer, psiFile);

            String artifactName = fetcher.getArtifactName(psiFile);
            if (!EcosMessages.confirm("Fetch artifact", String.format("Fetch %s from %s?", artifactName, sourceName), project)) {
                return;
            }

            try {
                String result = fetcher.fetch(ecosServer, psiFile);
                runUndoTransparentAction(() -> {
                    editor.getDocument().setText(result);
                    String message = String.format("%s successfully fetched from %s", artifactName, sourceName);
                    EcosMessages.info("Artifact fetched", message, project);
                });
            } catch (Exception e) {
                EcosMessages.error("Artifact fetching error", e.getMessage(), project);
            }
        });

    }

    @Override
    boolean canPerform(@NotNull AnActionEvent event) {
        return getFetcher(event) != null;
    }

    private FileFetcher getFetcher(AnActionEvent event) {

        PsiFile psiFile = getPsiFile(event);
        if (psiFile == null) {
            return null;
        }

        FileType fileType = resolveFileType(event);
        if (fileType == null) {
            return null;
        }

        return FileFetcher.EP_NAME
                .getExtensionsIfPointIsRegistered()
                .stream()
                .filter(fileFetcher -> fileFetcher.canFetch(psiFile, fileType))
                .findFirst()
                .orElse(null);

    }

}
