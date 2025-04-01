package ru.citeck.idea.artifacts.action.editor

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import ru.citeck.idea.artifacts.ArtifactTypeMeta
import ru.citeck.idea.artifacts.ArtifactsService
import ru.citeck.idea.artifacts.action.CiteckArtifactFileAction

class OpenArtifactDocsInBrowser : CiteckArtifactFileAction() {

    companion object {
        const val ARTIFACT_ACTION_ID = "open-docs-in-browser"
    }

    override fun isActionAllowed(event: AnActionEvent,
                                 file: PsiFile,
                                 project: Project,
                                 typeMeta: ArtifactTypeMeta
    ): Boolean {
        return typeMeta.docsUrl.isNotBlank()
    }

    override fun perform(event: AnActionEvent) {
        val docsUrl = ArtifactsService.getInstance().getArtifactTypeMeta(getPsiFile(event))?.docsUrl ?: ""
        if (docsUrl.isNotBlank()) {
            BrowserUtil.browse(docsUrl)
        }
    }

    override fun getArtifactActionId(): String {
        return ARTIFACT_ACTION_ID
    }
}
