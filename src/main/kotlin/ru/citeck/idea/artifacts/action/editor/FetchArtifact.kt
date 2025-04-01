package ru.citeck.idea.artifacts.action.editor

import com.intellij.openapi.actionSystem.AnActionEvent
import ru.citeck.idea.artifacts.ArtifactsService
import ru.citeck.idea.artifacts.action.CiteckArtifactFileAction
import ru.citeck.idea.utils.CiteckMessages

class FetchArtifact : CiteckArtifactFileAction() {

    companion object {
        const val ARTIFACT_ACTION_ID = "fetch"
    }

    override fun perform(event: AnActionEvent) {
        val project = event.project ?: return
        val psiFile = getPsiFile(event) ?: return
        ArtifactsService.getInstance().fetchArtifact(psiFile)
        CiteckMessages.info("Artifact fetched", "Artifact successfully fetched", project)
    }

    override fun getArtifactActionId(): String {
        return ARTIFACT_ACTION_ID
    }
}
