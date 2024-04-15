package ru.citeck.ecos.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.FileDeployer;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.settings.EcosServer;
import ru.citeck.ecos.utils.EcosMessages;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeployFile extends EcosAction {

    private record FileDeployerWrapper(String title, FileDeployer fileDeployer) {
        @Override
        public String toString() {
            return title;
        }
    }

    @Override
    protected void perform(@NotNull AnActionEvent event) {

        PsiFile psiFile = getPsiFile(event);
        if (psiFile == null) {
            return;
        }
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        List<FileDeployer> deployers = getDeployers(event);
        if (deployers.isEmpty()) {
            return;
        }

        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        EcosServer.doWithServer(project, ecosServer -> {
            if (deployers.size() == 1) {
                deploy(ecosServer, deployers.get(0), psiFile, project, editor);
                return;
            }

            List<FileDeployerWrapper> wrappers = deployers
                    .stream()
                    .map(fileDeployer -> new FileDeployerWrapper(
                            fileDeployer.getDestinationName(ecosServer, psiFile),
                            fileDeployer
                    ))
                    .collect(Collectors.toList());

            JBPopupFactory
                    .getInstance()
                    .createPopupChooserBuilder(wrappers)
                    .setTitle("Deploy To:")
                    .setItemChosenCallback(chosenDeployer -> deploy(ecosServer, chosenDeployer.fileDeployer, psiFile, project, editor))
                    .setRequestFocus(true)
                    .createPopup()
                    .showInCenterOf(WindowManager.getInstance().getFrame(event.getProject()).getRootPane());
        });

    }

    private void deploy(EcosServer ecosServer, FileDeployer deployer, @NotNull PsiFile psiFile, @NotNull Project project, Editor editor) {

        Optional
                .ofNullable(editor)
                .map(Editor::getDocument)
                .ifPresent(document -> PsiDocumentManager.getInstance(project).commitDocument(document));

        String destinationName = deployer.getDestinationName(ecosServer, psiFile);
        String artifactName = deployer.getArtifactName(psiFile);

        if (!EcosMessages.confirm("Deploy artifact", String.format("Deploy %s to %s?", artifactName, destinationName), project)) {
            return;
        }

        try {
            deployer.deploy(ecosServer, psiFile);
            EcosMessages.info(
                    "Artifact Deployed",
                    String.format("%s deployed to <b>%s</b>", artifactName, destinationName),
                    project
            );
        } catch (Exception e) {
            EcosMessages.error("Artifact deploying error", e.getMessage(), project);
        }

    }

    @Override
    boolean canPerform(@NotNull AnActionEvent event) {

        PsiFile psiFile = getPsiFile(event);
        if (psiFile == null) {
            return false;
        }

        Project project = event.getProject();
        if (project == null) {
            return false;
        }

        return !getDeployers(event).isEmpty();
    }

    private @NotNull List<FileDeployer> getDeployers(AnActionEvent event) {

        PsiFile psiFile = getPsiFile(event);
        if (psiFile == null) {
            return Collections.emptyList();
        }

        FileType fileType = resolveFileType(event);

        return FileDeployer.EP_NAME
                .getExtensionsIfPointIsRegistered()
                .stream()
                .filter(fileDeployer -> fileDeployer.canDeploy(psiFile, fileType))
                .collect(Collectors.toList());

    }

}
