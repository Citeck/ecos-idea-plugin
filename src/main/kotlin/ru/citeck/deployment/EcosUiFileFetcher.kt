package ru.citeck.deployment

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import ru.citeck.common.EcosServer
import ru.citeck.utils.EcosNotification
import ru.citeck.utils.EcosPrettyPrinter
import javax.swing.JOptionPane

class EcosUiFileFetcher : FileFetcher {

    private val objectWriter = ObjectMapper().writer(EcosPrettyPrinter())

    override fun canFetch(event: AnActionEvent): Boolean {
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return false
        if (virtualFile.fileType.name != "JSON") return false
        return EcosUiFileType.get(event) != null
    }

    override fun fetch(event: AnActionEvent) {
        val project = event.project ?: return
        val type = EcosUiFileType.get(event) ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val node = ObjectMapper().readValue(editor.document.text, JsonNode::class.java)
        val server = EcosServer.current()
        val id = node["id"].textValue() ?: return

        if (JOptionPane.showConfirmDialog(
                null,
                "Fetch ${type.typeName}/${id} from \"${server.url}\"?",
                "Fetch file",
                JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION
        ) {
            return
        }

        ApplicationManager.getApplication().runWriteAction {
            val response = server.execute(
                "/share/api/records/query?k=recs_count_1_uiserv%2F${type.typeName}",
                mapOf(
                    "records" to listOf("uiserv/${type.typeName}@${id}"),
                    "attributes" to listOf(".json")
                )
            )
            val text = objectWriter.writeValueAsString(response["records"][0]["attributes"][".json"]).replace("\r\n", "\n")
            CommandProcessor.getInstance().runUndoTransparentAction {
                editor.document.setText(text)
            }

            EcosNotification.Information(
                "File fetched",
                "File ${type.typeName}/${id} fetched from \"${server.url}\"",
                project
            )

        }

    }

}