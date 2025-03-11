package ru.citeck.idea.actions.file.artifact

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import ru.citeck.idea.actions.file.CiteckFileAction
import ru.citeck.idea.artifacts.ArtifactTypeMeta
import ru.citeck.idea.artifacts.ArtifactsService

abstract class CiteckArtifactAction : CiteckFileAction() {

    final override fun isActionAllowed(event: AnActionEvent, file: PsiFile, project: Project): Boolean {
        val meta = ArtifactsService.getInstance().getArtifactTypeMeta(file) ?: return false
        if (meta.disabledActions.contains(getArtifactActionId())) {
            return false
        }
        return isActionAllowed(event, file, project, meta)
    }

    open fun isActionAllowed(
        event: AnActionEvent,
        file: PsiFile,
        project: Project,
        typeMeta: ArtifactTypeMeta
    ): Boolean {
        return true
    }

    abstract fun getArtifactActionId(): String
}
