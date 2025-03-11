package ru.citeck.idea.actions.file.artifact

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import ru.citeck.idea.actions.file.CiteckFileAction
import ru.citeck.idea.artifacts.ArtifactsService
import ru.citeck.idea.utils.EcosMessages

open class DeployArtifact : CiteckArtifactAction() {

    companion object {
        const val ARTIFACT_ACTION_ID = "deploy"

        val log: Logger = Logger.getInstance(CiteckFileAction::class.java)
    }

    override fun perform(event: AnActionEvent) {
        val psiFile = getPsiFile(event) ?: return
        val project = event.project ?: return

        val artifactsService = ArtifactsService.getInstance()
        val artifactRef = artifactsService.getArtifactRef(psiFile)

        if (!EcosMessages.confirm("Deploy Artifact", "Deploy $artifactRef?", project)) {
            return
        }

        try {
            artifactsService.deployArtifact(psiFile)
            EcosMessages.info(
                "Artifact deployed",
                String.format("%s deployed", artifactRef),
                project
            )
        } catch (e: Exception) {
            log.error("Artifact deploying error", e)
            EcosMessages.error("Artifact deploying error", e.message, project)
        }
    }

    override fun getArtifactActionId(): String {
        return ARTIFACT_ACTION_ID
    }
}
