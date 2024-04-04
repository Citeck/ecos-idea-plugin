package ru.citeck.ecos.ui;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import icons.Icons;

public class AlfrescoJsConsoleService {

    private static final String TOOL_WINDOW_NAME = "Alfresco JS Console";

    private final Project project;

    private ConsoleView consoleView;
    private ToolWindow toolWindow;

    public AlfrescoJsConsoleService(Project project) {
        this.project = project;
    }

    public ConsoleView getConsoleView() {

        if (consoleView == null) {

            consoleView = TextConsoleBuilderFactory
                    .getInstance()
                    .createBuilder(project)
                    .getConsole();

            toolWindow = ToolWindowManager
                    .getInstance(project)
                    .registerToolWindow(
                            new RegisterToolWindowTask(
                                    TOOL_WINDOW_NAME,
                                    ToolWindowAnchor.BOTTOM,
                                    consoleView.getComponent(),
                                    false,
                                    false,
                                    false,
                                    true,
                                    null,
                                    Icons.AlfrescoLogo,
                                    null
                            ));
        }
        toolWindow.show();
        return consoleView;

    }

}
