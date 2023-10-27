package ru.citeck.ecos.actions;

import com.intellij.ide.actions.SearchEverywhereBaseAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.EcosSearchEveryWhereContributor;

public class EcosSearchEveryWhereAction extends SearchEverywhereBaseAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        boolean dumb = DumbService.isDumb(project);
        if (!dumb) {
            showInSearchEverywherePopup(EcosSearchEveryWhereContributor.class.getSimpleName(), e, true, false);
        }
    }

}
