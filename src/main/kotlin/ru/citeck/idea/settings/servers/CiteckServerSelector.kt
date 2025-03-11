package ru.citeck.idea.settings.servers

import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import icons.Icons
import ru.citeck.ecos.commons.promise.Promises
import ru.citeck.ecos.webapp.api.promise.Promise
import ru.citeck.idea.exceptions.GracefulAbortException
import ru.citeck.idea.settings.CiteckSettingsService
import java.util.concurrent.CompletableFuture
import javax.swing.JList

object CiteckServerSelector {

    private const val SELECT_ECOS_SERVER_TITLE = "Select Server:"

    @JvmStatic
    fun getServer(project: Project): Promise<CiteckServer> {
        val citeckServers = CiteckSettingsService.getInstance().getServers()
        val future = CompletableFuture<CiteckServer>()
        if (citeckServers.isEmpty()) {
            configureServers(project)
            future.completeExceptionally(GracefulAbortException("Servers is not configured"))
        } else if (citeckServers.size == 1) {
            future.complete(citeckServers[0])
        } else {
            JBPopupFactory
                .getInstance()
                .createPopupChooserBuilder(citeckServers)
                .setTitle(SELECT_ECOS_SERVER_TITLE)
                .setNamerForFiltering { ecosServer: CiteckServer -> ecosServer.name + "|" + ecosServer.host }
                .setRenderer(EcosServerColoredListCellRenderer())
                .setItemChosenCallback { future.complete(it) }
                .setRequestFocus(true)
                .createPopup()
                .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)
        }
        return Promises.create(future)
    }

    private fun configureServers(project: Project) {
        JBPopupFactory
            .getInstance()
            .createPopupChooserBuilder(listOf("Configure servers..."))
            .setTitle(SELECT_ECOS_SERVER_TITLE)
            .setItemChosenCallback {
                ShowSettingsUtil.getInstance().showSettingsDialog(
                    project,
                    CiteckServersConfiguration::class.java
                )
            }
            .setRequestFocus(true)
            .createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)
    }

    private class EcosServerColoredListCellRenderer : ColoredListCellRenderer<CiteckServer>() {

        override fun customizeCellRenderer(
            list: JList<out CiteckServer>,
            value: CiteckServer,
            index: Int,
            selected: Boolean,
            hasFocus: Boolean
        ) {
            setPaintFocusBorder(false)
            icon = Icons.CiteckLogo
            font = list.font
            append(
                value.name.ifBlank { value.host.substringAfter(':') },
                SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, list.foreground),
                true
            )
            append(" " + value.host, SimpleTextAttributes.GRAY_SMALL_ATTRIBUTES)
        }
    }
}
