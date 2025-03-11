package ru.citeck.idea.actions.file.artifact

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import ru.citeck.idea.artifacts.ArtifactsService

class OpenArtifactInBrowser : CiteckArtifactAction() {

    companion object {
        const val ARTIFACT_ACTION_ID = "open-in-browser"
    }

    override fun perform(event: AnActionEvent) {
        val psiFile = getPsiFile(event) ?: return
        val url = ArtifactsService.getInstance().getArtifactUrl(psiFile).get()
        BrowserUtil.browse(url)
    }

    override fun getArtifactActionId(): String {
        return ARTIFACT_ACTION_ID
    }
}
