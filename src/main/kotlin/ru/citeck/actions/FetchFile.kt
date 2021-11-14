package ru.citeck.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import ru.citeck.deployment.EcosUiFileFetcher
import ru.citeck.deployment.FileFetcher

class FetchFile : AnAction() {

    private val fetchers = listOf(
        EcosUiFileFetcher()
    )

    override fun update(event: AnActionEvent) {
        event.presentation.isVisible = getFetcher(event) != null
    }

    private fun getFetcher(event: AnActionEvent): FileFetcher? {
        fetchers.forEach { fetcher ->
            if (fetcher.canFetch(event)) {
                return fetcher
            }
        }
        return null
    }

    override fun actionPerformed(event: AnActionEvent) {
        getFetcher(event)!!.fetch(event)
    }

}