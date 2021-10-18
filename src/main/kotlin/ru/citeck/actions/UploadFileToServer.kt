package ru.citeck.actions

import com.fasterxml.jackson.databind.JsonNode
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import ru.citeck.RestApiUtils
import java.util.*
import javax.swing.JOptionPane

class UploadFileToServer : AnAction() {

    var type: String = ""

    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = false
        val vFile = e.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        if (!vFile.fileType.name.equals("JSON")) return

        val directory = vFile.parent.path
        type = ""
        if (directory.endsWith("/ecos-forms")) {
            type = "eform"
            e.presentation.text = "Upload Form to server"
//TODO: Требует авторизации для загрузки дашбордов
//        } else if (directory.endsWith("/ui/dashboard")) {
//            type = "dashboard"
//            e.presentation.text = "Upload Dashboard to server"
        } else if (directory.endsWith("ui/journal")) {
            type = "journal"
            e.presentation.text = "Upload Journal to server"
        } else {
            return
        }

        e.presentation.isVisible = true
    }


    private class Document() {
        var id: String = ""
    }


    private class Request(type: String, id: String, json: String) {
        val records = listOf(
            mapOf(
                "id" to "uiserv/$type@",
                "attributes" to mapOf(
                    ".att(n:\"_content\"){as(n:\"content-data\"){json}}" to listOf(
                        mapOf(
                            "storage" to "base64",
                            "name" to "${json}.json",
                            "url" to "data:application/json;base64,${
                                Base64.getEncoder().encodeToString(json.toByteArray(Charsets.UTF_8))
                            }",
                            "type" to "application/json",
                            "originalName" to "${id}.json"
                        )
                    )
                )
            )
        )
    }


    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        try {
            val json = RestApiUtils.convert(editor.document.text, Document::class.java)
            if (JOptionPane.showConfirmDialog(
                    null,
                    "Upload ${type}/${json.id} to server?",
                    e.presentation.text + "?",
                    JOptionPane.YES_NO_OPTION
                ) != JOptionPane.YES_OPTION
            ) {
                return
            }

            val response = RestApiUtils.execute(
                "http://localhost:8081/api/records/mutate?k=recs_count_1_",
                Request(type, json.id, editor.document.text),
                JsonNode::class.java
            )

            println(response)

        } catch (ex: Exception) {
            JOptionPane.showMessageDialog(null, "Unable to upload file to server.\r\nIncorrect format.")
            return
        }

    }

}