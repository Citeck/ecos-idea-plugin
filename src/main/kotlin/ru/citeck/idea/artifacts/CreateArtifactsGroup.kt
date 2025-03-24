package ru.citeck.idea.artifacts

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.DumbAware
import icons.Icons
import ru.citeck.idea.project.CiteckProject

class CreateArtifactsGroup : DefaultActionGroup("Citeck Artifact", "", Icons.CiteckLogo), DumbAware {

    init {
        isPopup = true
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = isGroupAvailable(e.dataContext)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    private fun isGroupAvailable(dataContext: DataContext): Boolean {
        val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return false
        return CiteckProject.getInstance(project)
            .isCiteckModule(LangDataKeys.MODULE.getData(dataContext))
    }
}
