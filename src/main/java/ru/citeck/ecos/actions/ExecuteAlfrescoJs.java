package ru.citeck.ecos.actions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.scratch.ScratchUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.ui.KeyValueInputDialog;
import ru.citeck.ecos.utils.EcosMessages;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecuteAlfrescoJs extends EcosAction {

    @Override
    protected void perform(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            return;
        }
        executeAlfrescoJs(project, editor.getDocument().getText());
    }

    @Override
    boolean canPerform(@NotNull AnActionEvent event) {
        VirtualFile virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (virtualFile == null) {
            return false;
        }
        Project project = event.getProject();
        if (project == null) {
            return false;
        }
        return FileExtensionFilter.JS.accept(virtualFile, project) && ScratchUtil.isScratch(virtualFile);
    }

    private Map<String, String> getParameters(String text) {
        LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
        Matcher matcher = Pattern.compile("#\\{[a-zA-Z0-9_]*}").matcher(text);
        while (matcher.find()) {
            String param = matcher.group();
            parameters.put(param.substring(2, param.length() - 1), "");
        }
        return parameters;
    }

    public void executeAlfrescoJs(Project project, String text) {

        var ref = new Object() {
            String jsText = text;
        };

        Map<String, String> parameters = getParameters(text);
        if (parameters.size() > 0) {
            var dialog = new KeyValueInputDialog(parameters, "Parameter", "Value");
            dialog.setTitle("Set Parameters for Script:");
            if (!dialog.showAndGet()) {
                return;
            }
            parameters.forEach((key, value) -> ref.jsText = ref.jsText.replace("#{" + key + "}", value));
        }

        ConsoleView consoleView = ServiceRegistry
            .getAlfrescoJsConsoleService(project)
            .getConsoleView();
        consoleView.clear();

        consoleView.print("Executing script...\n", ConsoleViewContentType.LOG_INFO_OUTPUT);

        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Executing Alfresco JS") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    JsonNode response = ServiceRegistry.getEcosRestApiService().executeJS(ref.jsText);
                    if (response.has("message")) {
                        consoleView.print(response.get("message").asText(), ConsoleViewContentType.ERROR_OUTPUT);
                        return;
                    }
                    String scriptPerf = response.get("scriptPerf").asText();
                    scriptPerf = "Script executed in " + scriptPerf + "ms.";
                    EcosMessages.info("Alfresco JS executed", scriptPerf, project);
                    consoleView.print(scriptPerf + "\n", ConsoleViewContentType.LOG_INFO_OUTPUT);
                    ArrayNode printOutput = (ArrayNode) response.get("printOutput");
                    printOutput.forEach(
                        jsonNode -> consoleView.print(jsonNode.asText() + "\n", ConsoleViewContentType.NORMAL_OUTPUT)
                    );
                } catch (Exception ex) {
                    consoleView.print("Alfresco Js execution error:\n\n", ConsoleViewContentType.LOG_ERROR_OUTPUT);
                }
            }
        });

    }

}