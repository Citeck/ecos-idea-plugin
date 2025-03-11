package ru.citeck.idea.actions

import com.intellij.ide.actions.SearchEverywhereBaseAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbService
import ru.citeck.idea.CiteckSearchEveryWhereContributor

class CiteckSearchEverywhereAction : SearchEverywhereBaseAction() {

    override fun actionPerformed(event: AnActionEvent) {

        val project = event.project ?: return

        val dumb: Boolean = DumbService.isDumb(project)
        if (!dumb) {
            showInSearchEverywherePopup(
                CiteckSearchEveryWhereContributor::class.java.simpleName,
                event,
                true,
                false
            )
        }
    }
}
