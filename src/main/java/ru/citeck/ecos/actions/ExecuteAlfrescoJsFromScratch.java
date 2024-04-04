package ru.citeck.ecos.actions;

import com.intellij.ide.scratch.ScratchUtil;
import com.intellij.ide.scratch.ScratchesSearchScope;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.search.FilenameIndex;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.utils.EcosMessages;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class ExecuteAlfrescoJsFromScratch extends EcosAction {

    @Override
    protected void perform(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        JBPopupFactory
                .getInstance()
                .createPopupChooserBuilder(
                        FilenameIndex.getAllFilesByExt(project, "js", ScratchesSearchScope.getScratchesScope(project))
                                .stream()
                                .map(virtualFile -> new JsFileWrapper(project, virtualFile))
                                .collect(Collectors.toList())
                )
                .setTitle("Execute Alfresco JS:")
                .setItemChosenCallback(jsFileWrapper -> executeJS(project, jsFileWrapper))
                .setNamerForFiltering(JsFileWrapper::toString)
                .setRequestFocus(true)
                .createPopup()
                .showInCenterOf(WindowManager.getInstance().getFrame(project).getRootPane());
    }

    @Override
    boolean canPerform(@NotNull AnActionEvent event) {
        return true;
    }

    private void executeJS(Project project, JsFileWrapper jsFileWrapper) {
        try (InputStream inputStream = jsFileWrapper.getVirtualFile().getInputStream()) {
            ExecuteAlfrescoJs action = (ExecuteAlfrescoJs) ActionManager.getInstance().getAction("Ecos.ExecuteAlfrescoJs");
            String script = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            action.executeAlfrescoJs(project, script);
        } catch (Exception ex) {
            EcosMessages.error("Error while executing Alfresco JS", ex.getMessage(), project);
        }
    }

    private static class JsFileWrapper {

        private final VirtualFile virtualFile;
        private final String relativePath;

        public JsFileWrapper(Project project, VirtualFile virtualFile) {
            this.virtualFile = virtualFile;
            String relativePath = ScratchUtil.getRelativePath(project, virtualFile);
            if (relativePath.startsWith("Scratches")) {
                relativePath = relativePath.substring("Scratches".length());
            }
            this.relativePath = relativePath;
        }

        @Override
        public String toString() {
            return relativePath;
        }

        public VirtualFile getVirtualFile() {
            return virtualFile;
        }
    }

}
