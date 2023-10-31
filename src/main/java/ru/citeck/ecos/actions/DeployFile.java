package ru.citeck.ecos.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.FileDeployer;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.utils.EcosMessages;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeployFile extends EcosAction {

    public static final ExtensionPointName<FileDeployer> EP_NAME =
        ExtensionPointName.create("ru.citeck.ecos.fileDeployer");

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

        if (deployers.size() == 1) {
            deploy(deployers.get(0), psiFile, project, editor);
            return;
        }

        List<FileDeployerWrapper> wrappers = deployers
            .stream()
            .map(fileDeployer -> new FileDeployerWrapper(
                fileDeployer.getDestinationName(psiFile),
                fileDeployer
            ))
            .collect(Collectors.toList());

        JBPopupFactory
            .getInstance()
            .createPopupChooserBuilder(wrappers)
            .setTitle("Deploy to:")
            .setItemChosenCallback(chosenDeployer -> deploy(chosenDeployer.fileDeployer, psiFile, project, editor))
            .setRequestFocus(true)
            .createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(event.getProject()).getRootPane());

    }

    private void deploy(FileDeployer deployer, @NotNull PsiFile psiFile, @NotNull Project project, Editor editor) {

        Optional
            .ofNullable(editor)
            .map(Editor::getDocument)
            .ifPresent(document -> PsiDocumentManager.getInstance(project).commitDocument(document));

        String destinationName = deployer.getDestinationName(psiFile);
        String fileName = psiFile.getVirtualFile().getName();

        if (!EcosMessages.confirm(
            "Deploy file",
            String.format("Deploy %s to %s?", fileName, destinationName))
        ) {
            return;
        }

        try {
            deployer.deploy(psiFile);
            EcosMessages.info(
                "File deployed",
                String.format("File %s deployed to <b>%s</b>", fileName, destinationName),
                project
            );
        } catch (Exception e) {
            EcosMessages.error("File deploying error", e.getMessage(), project);
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

        return EP_NAME
            .extensions()
            .filter(fileDeployer -> fileDeployer.canDeploy(psiFile, fileType))
            .collect(Collectors.toList());

    }

    @RequiredArgsConstructor
    private static class FileDeployerWrapper {
        final String title;
        final FileDeployer fileDeployer;

        @Override
        public String toString() {
            return title;
        }
    }

}
