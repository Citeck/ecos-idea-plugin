package ru.citeck.idea.search

import com.intellij.ide.actions.SearchEverywhereBaseAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbService

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
