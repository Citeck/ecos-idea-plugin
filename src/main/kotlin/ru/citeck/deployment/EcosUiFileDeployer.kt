package ru.citeck.deployment

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import ru.citeck.common.EcosServer
import ru.citeck.utils.EcosNotification
import java.util.*
import javax.swing.JOptionPane

class EcosUiFileDeployer : FileDeployer {


    override fun canDeploy(event: AnActionEvent): Boolean {
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return false
        if (virtualFile.fileType.name != "JSON") return false
        return EcosUiFileType.get(event) != null
    }

    override fun deploy(event: AnActionEvent) {
        val project = event.project ?: return
        val type = EcosUiFileType.get(event) ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val node = ObjectMapper().readValue(editor.document.text, JsonNode::class.java)
        val server = EcosServer.current()
        val id = node["id"].textValue() ?: return

        if (JOptionPane.showConfirmDialog(
                null,
                "Deploy ${type.typeName}/${id} to \"${server.url}\"?",
                "Deploy file",
                JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION
        ) {
            return
        }

        ApplicationManager.getApplication().runWriteAction {
            val response = server.execute(
                "/share/api/records/mutate?k=recs_count_1_",
                mapOf(
                    "records" to listOf(
                        mapOf(
                            "id" to "uiserv/${type.typeName}@",
                            "attributes" to mapOf(
                                ".att(n:\"_content\"){as(n:\"content-data\"){json}}" to listOf(
                                    mapOf(
                                        "storage" to "base64",
                                        "name" to "${id}.json",
                                        "url" to "data:application/json;base64,${
                                            Base64.getEncoder()
                                                .encodeToString(editor.document.text.toByteArray(Charsets.UTF_8))
                                        }",
                                        "type" to "application/json",
                                        "originalName" to "${id}.json"
                                    )
                                )
                            )
                        )
                    )
                )
            )

            EcosNotification.Information(
                "File deployed",
                "File ${type.typeName}/${id} deployed to \"${server.url}\"",
                project
            )

        }
    }

}