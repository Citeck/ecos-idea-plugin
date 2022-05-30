package ru.citeck.deployment

import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.xml.XmlFile
import com.intellij.ui.components.JBTextField
import ru.citeck.common.EcosServer
import ru.citeck.utils.EcosNotification
import javax.swing.JComponent

class EcosCaseTemplateFetcher : FileFetcher {

    companion object {
        private val lastNodeRefs = HashMap<String, String>()
    }

    private class NodeRefInputDialog(val nodeRef: String) : DialogWrapper(true) {

        val textField: JBTextField

        init {
            title = "Input NodeRef with case template:"
            textField = JBTextField(nodeRef)
            init()
        }

        override fun createCenterPanel(): JComponent {
            return textField
        }

        override fun getPreferredFocusedComponent(): JComponent {
            return textField
        }
    }

    override fun canFetch(event: AnActionEvent): Boolean {
        val psiFile = event.getData(PlatformDataKeys.PSI_FILE) ?: return false
        if (!(psiFile is XmlFile)) return false
        return psiFile.isValid && psiFile.rootTag?.namespace == "http://www.omg.org/spec/CMMN/20151109/MODEL"
    }

    override fun fetch(event: AnActionEvent) {

        val project = event.project ?: return
        val psiFile = event.getData(PlatformDataKeys.PSI_FILE) as XmlFile? ?: return
        val fileName = psiFile.virtualFile.path;

        val lastNodeRef = lastNodeRefs[fileName] ?: ""

        val nodeRefInputDialog = NodeRefInputDialog(lastNodeRef);
        if (!nodeRefInputDialog.showAndGet()) {
            return
        }
        val nodeRef = nodeRefInputDialog.textField.text
        lastNodeRefs[fileName] = nodeRef

        val server = EcosServer.current()
        val response = server.execute("share/proxy/alfresco/citeck/case/template?nodeRef=${nodeRef}");
        if (!response.has("success")) {
            EcosNotification.Error(
                "Failed to fetch case template",
                response.get("originalMessage").textValue(),
                project
            )
            return
        }
        val template = response.get("template").textValue();
        val content = server.execute(
            "share/proxy/alfresco/citeck/ecos/records/query?k=recs_count_1_", mapOf(
                "records" to listOf(template),
                "attributes" to listOf("cm:content?disp")
            )
        )
        val recordAttributes = content.get("records")[0].get("attributes")
        val xml = recordAttributes.get("cm:content?disp").textValue()

        val xmlPsi = PsiFileFactory.getInstance(event.project!!).createFileFromText(
            Language.findLanguageByID("XML")!!, xml
        ) as XmlFile? ?: return

        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().runUndoTransparentAction {
                val newCase = xmlPsi.rootTag?.findFirstSubTag("cmmn:case") ?: return@runUndoTransparentAction
                newCase.attributes.forEach { it.delete() }
                newCase.children.forEach {
                    if (it is PsiWhiteSpace) it.delete()
                }
                psiFile.rootTag?.findFirstSubTag("cmmn:case")?.attributes?.forEach {
                    xmlPsi.rootTag?.findFirstSubTag("cmmn:case")?.setAttribute(it.name, it.value)
                }
                psiFile.rootTag?.delete()
                psiFile.add(xmlPsi.rootTag!!)
            }
        }

        EcosNotification.Information(
            "Case template fetched",
            "Case template ${psiFile.name} fetched from nodeRef \"${nodeRef}\"  (\"${server.url}\")",
            project
        )


    }

}