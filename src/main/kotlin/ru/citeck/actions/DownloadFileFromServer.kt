package ru.citeck.actions

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import ru.citeck.RestApiUtils
import ru.citeck.utils.EcosPrettyPrinter
import javax.swing.JOptionPane


class DownloadFileFromServer : AnAction() {

    private var type: String = ""
    private val objectWriter = ObjectMapper().writer(EcosPrettyPrinter())

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = false
        val vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        if (!vFile.fileType.name.equals("JSON")) return

        val directory = vFile.parent.path
        type = ""
        if (directory.endsWith("/ecos-forms")) {
            type = "eform"
            e.presentation.text = "Download Form from server"
        } else if (directory.endsWith("/ui/dashboard")) {
            type = "dashboard"
            e.presentation.text = "Download Dashboard from server"
        } else if (directory.endsWith("ui/journal")) {
            type = "journal"
            e.presentation.text = "Download Journal from server"
        } else {
            return
        }
        e.presentation.isVisible = true
    }

    private class Request(val records: List<String>) {
        val attributes = listOf(".json")
    }

    private class Response() {
        var records: List<JsonNode> = listOf()
    }

    private class Document() {
        var id: String = ""
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        try {
            val jsonDoc = RestApiUtils.convert(editor.document.text, Document::class.java);
            if (JOptionPane.showConfirmDialog(
                    null,
                    "Download ${type}/${jsonDoc.id} from server?",
                    e.presentation.text + "?",
                    JOptionPane.YES_NO_OPTION
                ) != JOptionPane.YES_OPTION
            ) {
                return
            }
            ApplicationManager.getApplication().runWriteAction {
                val response = RestApiUtils.execute(
                    "http://localhost:8081/api/records/query",
                    Request(listOf("${type}@${jsonDoc.id}")),
                    Response::class.java
                )
                val json = objectWriter
                    .writeValueAsString(response.records[0].get("attributes").get(".json")).replace("\r\n", "\n")
                CommandProcessor.getInstance().runUndoTransparentAction {
                    editor.document.setText(json)
                }

            }

        } catch (ex: Exception) {
            JOptionPane.showMessageDialog(null, "Unable to download file from server.\r\nIncorrect format.")
            return
        }

    }

}