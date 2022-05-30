package ru.citeck.deployment

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.psi.xml.XmlFile
import ru.citeck.common.EcosServer
import ru.citeck.utils.EcosNotification
import java.util.*
import javax.swing.JOptionPane

class EcosCaseTemplateDeployer : FileDeployer {

    override fun canDeploy(event: AnActionEvent): Boolean {
        val psiFile = event.getData(PlatformDataKeys.PSI_FILE) ?: return false
        if (!(psiFile is XmlFile)) return false
        return psiFile.isValid && psiFile.rootTag?.namespace == "http://www.omg.org/spec/CMMN/20151109/MODEL"
    }

    override fun deploy(event: AnActionEvent) {

        val server = EcosServer.current()
        val project = event.project ?: return
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return

        if (JOptionPane.showConfirmDialog(
                null,
                "Deploy case template ${virtualFile.name} to \"${server.url}\"?",
                "Deploy case template",
                JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION
        ) {
            return
        }

        runBackgroundableTask("Deploying case template") {

            val response = server.execute(
                "/share/api/records/mutate?k=recs_count_1_",
                mapOf(
                    "records" to listOf(
                        mapOf(
                            "id" to "eproc/procdef@",
                            "attributes" to mapOf(
                                ".att(n:\"_content\"){as(n:\"content-data\"){json}}" to listOf(
                                    mapOf(
                                        "storage" to "base64",
                                        "name" to "${virtualFile.name}",
                                        "url" to "data:text/xml;base64,${
                                            Base64.getEncoder()
                                                .encodeToString(editor.document.text.toByteArray(Charsets.UTF_8))
                                        }",
                                        "type" to "text/xml",
                                        "originalName" to "${virtualFile.name}"
                                    )
                                )
                            )
                        )
                    )
                )
            )


            val jsResponse = EcosServer.current().executeJs(
                "var srv = services.get('eprocActivityService');\n" +
                        "var cache1 = Packages.org.apache.commons.lang.reflect.FieldUtils.readField(srv, 'typesToRevisionIdCache', true);\n" +
                        "cache1.invalidateAll();\n" +
                        "var cache2 = Packages.org.apache.commons.lang.reflect.FieldUtils.readField(srv, 'revisionIdToProcessDefinitionCache', true);\n" +
                        "cache2.invalidateAll();"
            )

            EcosNotification.Information(
                "Case template deployed",
                "Case template ${virtualFile.name} deployed to \"${server.url}\"",
                project
            )

        }

    }
}