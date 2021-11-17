package ru.citeck.actions

import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.icons.AllIcons
import com.intellij.ide.BrowserUtil
import com.intellij.ide.scratch.ScratchUtil
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.table.JBTable
import ru.citeck.EcosServer
import ru.citeck.utils.HashMapTableModel
import java.awt.Dimension
import java.awt.MouseInfo
import java.net.URLEncoder
import javax.swing.JComponent
import kotlin.collections.HashMap

class ExecuteAlfrescoJS : AnAction() {

    companion object {
        private val consoles = HashMap<ToolWindow, ConsoleView>()
    }

    private class Response() {
        var message: String = ""
        var printOutput: List<String> = listOf()
        var scriptPerf: String = ""
    }


    private fun getConsole(project: Project): ConsoleView {

        val toolWindowManager = ToolWindowManager.getInstance(project)
        var toolWindow = toolWindowManager.getToolWindow("Alfresco JS Console")

        if (toolWindow == null) {
            val console = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
            toolWindow = ToolWindowManager.getInstance(project).registerToolWindow(
                RegisterToolWindowTask(
                    id = "Alfresco JS Console",
                    anchor = ToolWindowAnchor.BOTTOM,
                    component = console.component,
                    icon = AllIcons.Debugger.Console,
                    canCloseContent = false
                )
            )
            consoles.put(toolWindow, console)
        }
        toolWindow.show()
        return consoles[toolWindow]!!
    }


    private class ParamInputDialog(val params: HashMap<String, String>): DialogWrapper(true) {

        val table: JBTable

        init {
            title = "Set parameters for script:"
            table = JBTable(HashMapTableModel(params, keyColumnName = "Parameter"));
            init()
        }

        override fun createCenterPanel(): JComponent {
            return ToolbarDecorator.createDecorator(table).setMinimumSize(Dimension(480, 640)).createPanel()
        }

        override fun getPreferredFocusedComponent(): JComponent {
            return table
        }
    }


    fun executeJs(project: Project, text: String) {

        var jsText = text

        val params = LinkedHashMap<String, String>()
        Regex("#\\{[a-zA-Z0-9_]*\\}").findAll(text).forEach {
            params[it.value.substring(2, it.value.length - 1)] = ""
        }

        if (params.size > 0) {
            if (!ParamInputDialog(params).showAndGet()) return
            params.entries.forEach { param ->
                jsText = jsText.replace("#{${param.key}}", param.value)
            }
        }

        val console = getConsole(project)
        val server = EcosServer.current()
        console.clear()
        console.print("Executing script...", ConsoleViewContentType.LOG_INFO_OUTPUT)

        ApplicationManager.getApplication().executeOnPooledThread {
            ApplicationManager.getApplication().runReadAction {
                val response = server.execute(
                    "share/proxy/alfresco/de/fme/jsconsole/execute",
                    mapOf(
                        "script" to jsText,
                        "runas" to "admin",
                        "template" to "",
                        "spaceNodeRef" to "",
                        "transaction" to "readwrite",
                        "urlargs" to "",
                        "documentNodeRef" to ""
                    ),
                    Response::class.java
                )
                console.clear()
                if (response.message != "") {
                    console.print(response.message, ConsoleViewContentType.LOG_ERROR_OUTPUT)
                } else {
                    console.print(
                        "Script executed in ${response.scriptPerf}ms.\r\n",
                        ConsoleViewContentType.LOG_INFO_OUTPUT
                    )
                    val pattern =
                        "workspace://SpacesStore/[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}"
                    val regexSplit = Regex("((?<=${pattern})|(?=${pattern}))")
                    val regexNodeRef = Regex(pattern)

                    response.printOutput.forEach { it ->
                        regexSplit.split(it).forEach { nodeRef ->
                            if (regexNodeRef.matches(nodeRef)) {
                                console.printHyperlink(nodeRef) { project -> openBrowser(project, nodeRef) }
                            } else {
                                console.print(nodeRef, ConsoleViewContentType.NORMAL_OUTPUT)
                            }
                        }
                        console.print("\r\n", ConsoleViewContentType.NORMAL_OUTPUT)
                    }
                }
            }
        }
    }


    override fun actionPerformed(e: AnActionEvent) {
        if (!e.presentation.isVisible) return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.project ?: return
        val text = editor.document.text
        executeJs(project, text)
    }


    private class UrlPopupItem(val title: String, val url: String) {
        override fun toString(): String {
            return title
        }
    }


    private fun openBrowser(project: Project, nodeRef: String) {
        val server = EcosServer.current()
        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(
                listOf(
                    UrlPopupItem(
                        "Node browser",
                        "${server.url}/share/page/console/admin-console/node-browser#state=panel" + URLEncoder.encode(
                            "=view&nodeRef=${nodeRef}&search=${nodeRef}&lang=noderef&store=workspace%3A%2F%2FSpacesStore"
                        )
                    ),
                    UrlPopupItem("Card details", "${server.url}/share/page/card-details?&nodeRef=${nodeRef}"),
                    UrlPopupItem(
                        "Card details (old)",
                        "${server.url}/share/page/card-details?&forceOld=true&nodeRef=${nodeRef}"
                    )
                )
            )
            .setTitle("Browse node in:")
            .setItemChosenCallback { BrowserUtil.browse(it.url, project) }
            .setRequestFocus(true).createPopup()
            .show(
                RelativePoint.fromScreen(MouseInfo.getPointerInfo().location)
            )
    }


    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = false
        val vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        val fileType = vFile.fileType.name
        e.presentation.isVisible = (fileType.equals("JavaScript") || vFile.extension?.toLowerCase().equals("js"))
                && ScratchUtil.isScratch(vFile)
    }


}