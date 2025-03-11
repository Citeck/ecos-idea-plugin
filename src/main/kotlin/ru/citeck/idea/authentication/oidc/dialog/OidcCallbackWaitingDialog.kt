package ru.citeck.idea.authentication.oidc.dialog

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.groovy.util.Maps
import ru.citeck.ecos.commons.promise.Promises
import ru.citeck.ecos.webapp.api.promise.Promise
import ru.citeck.idea.authentication.oidc.OidcAuthParams
import ru.citeck.idea.authentication.oidc.OidcServerInfo
import ru.citeck.idea.authentication.oidc.callback.OidcCallbackData
import ru.citeck.idea.authentication.oidc.callback.OidcCallbackServer
import ru.citeck.idea.exceptions.GracefulAbortException
import java.awt.Container
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.swing.*
import javax.swing.Timer
import kotlin.math.max

object OidcCallbackWaitingDialog {

    private val LOGIN_TIMEOUT: Duration = Duration.ofMinutes(2)

    fun show(project: Project, args: OidcAuthParams, serverInfo: OidcServerInfo): Promise<OidcCallbackData> {
        val authEndpoint = serverInfo.authEndpoint
        if (StringUtils.isBlank(authEndpoint)) {
            return Promises.reject(RuntimeException("authEndpoint is empty"))
        }

        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)

        val dialog = CustomWrapper(project, panel)
        dialog.title = "Please Log in Using Your Browser"
        dialog.setSize(400, 120)
        dialog.isModal = true
        dialog.isResizable = false
        dialog.isOKActionEnabled = false

        val labelText = "Please log in using your browser within %d seconds."

        val timeLabel = JLabel(String.format(labelText, LOGIN_TIMEOUT.toSeconds()), SwingConstants.CENTER)
        panel.add(timeLabel, SwingConstants.CENTER)

        val state = createRandomId()

        val browserUrl = buildUrlWithParams(
            serverInfo.authEndpoint,
            Maps.of(
                "client_id", args.clientId,
                "redirect_uri", args.redirectUri,
                "response_type", "code",
                "scope", "openid",
                "state", state,
                "nonce", createRandomId()
            )
        )

        val waitUntilMs = System.currentTimeMillis() + LOGIN_TIMEOUT.toMillis()
        val timoutTimer = Timer(1000) { _ ->
            val secondsLeft = max(((waitUntilMs - System.currentTimeMillis()) / 1000).toDouble(), 0.0).toLong()
            timeLabel.text = String.format(labelText, secondsLeft)
        }

        val oidcCallbackServer = ApplicationManager.getApplication()
            .getService(OidcCallbackServer::class.java)

        val callbackData = CompletableFuture<OidcCallbackData>()

        val callbackServer = oidcCallbackServer.startServer(args.callbackPort, callbackData, LOGIN_TIMEOUT)

        val callbackDataPromise = Promises.create(callbackData).then { data: OidcCallbackData ->
            if (data.state != state) {
                error("OIDC Callback state doesn't match")
            }
            data
        }.finally {
            // Using SwingUtilities.invokeLater instead of ApplicationManager.getApplication().invokeLater
            // because invokeLater from ApplicationManager executes the provided code only after the dialog
            // is closed manually. However, we need to close the dialog programmatically right away,
            // so SwingUtilities.invokeLater ensures the closure happens in the correct thread and immediately
            // after the current code execution.
            SwingUtilities.invokeLater { dialog.close(DialogWrapper.OK_EXIT_CODE) }
        }

        timoutTimer.start()
        BrowserUtil.browse(browserUrl)
        dialog.show()
        IOUtils.closeQuietly(callbackServer)
        timoutTimer.stop()

        if (!callbackData.isDone) {
            callbackData.completeExceptionally(GracefulAbortException("Cancelled"))
        }

        return callbackDataPromise
    }

    private fun createRandomId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }

    private fun buildUrlWithParams(url: String, params: Map<String, String>): String {
        val result = StringBuilder(url)
        if (!url.contains("?")) {
            result.append("?")
        }
        params.forEach { (k: String?, v: String?) ->
            result.append(k)
                .append("=")
                .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
                .append("&")
        }
        result.setLength(result.length - 1)

        return result.toString()
    }

    private class CustomWrapper(
        project: Project,
        private val container: Container
    ) : DialogWrapper(project) {
        init {
            init()
        }
        override fun createCenterPanel(): JComponent {
            return container as JComponent
        }
    }
}
