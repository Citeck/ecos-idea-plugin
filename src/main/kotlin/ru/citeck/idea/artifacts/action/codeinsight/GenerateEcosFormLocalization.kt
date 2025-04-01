package ru.citeck.idea.artifacts.action.codeinsight

import com.intellij.codeInsight.actions.SimpleCodeInsightAction
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiFile
import ecos.com.fasterxml.jackson210.databind.JsonNode
import ecos.com.fasterxml.jackson210.databind.ObjectMapper
import ecos.com.fasterxml.jackson210.databind.node.ObjectNode
import ecos.com.fasterxml.jackson210.databind.node.TextNode
import ru.citeck.ecos.commons.json.Json.mapper
import ru.citeck.idea.artifacts.ArtifactTypes
import ru.citeck.idea.utils.CiteckMessages
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport
import javax.swing.ListSelectionModel

class GenerateEcosFormLocalization : SimpleCodeInsightAction() {

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        try {
            val objectMapper = ObjectMapper()
            val jsonNode = objectMapper.readValue(
                editor.document.text,
                JsonNode::class.java
            )

            val fields = Stream.of("/i18n/ru", "/i18n/en")
                .map { path: String? -> jsonNode.at(path).fieldNames() }
                .flatMap { iterator: Iterator<String>? ->
                    StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(iterator, Spliterator.NONNULL), false
                    )
                }
                .collect(Collectors.toSet())

            val messages = jsonNode
                .findParents("type")
                .stream()
                .filter { node: JsonNode ->
                    val type = node["type"]
                    type is TextNode && "asyncData" != type.textValue()
                }
                .flatMap { node: JsonNode ->
                    Stream.of(
                        node["label"], node["description"], node["title"]
                    )
                }
                .filter { node: JsonNode? -> node is TextNode }
                .map { obj: JsonNode -> obj.textValue() }
                .filter { message: String? -> !(message.isNullOrBlank() || fields.contains(message)) }
                .distinct()
                .collect(Collectors.toList())

            JBPopupFactory
                .getInstance()
                .createPopupChooserBuilder(messages)
                .setTitle("Select Messages for Localization:")
                .setNamerForFiltering { obj: String -> obj }
                .setRequestFocus(true)
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
                .setItemsChosenCallback { selectedMessages: Set<String> ->
                    addLocalization(
                        selectedMessages,
                        jsonNode,
                        editor
                    )
                }
                .createPopup()
                .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)
        } catch (ex: Exception) {
            CiteckMessages.error("Error while insert component", "Incorrect file content", project)
        }
    }

    private fun addLocalization(messages: Set<String>, jsonNode: JsonNode, editor: Editor) {
        val json: String
        try {
            messages.forEach { message: String? ->
                (jsonNode.at("/i18n/ru") as ObjectNode).put(message, "")
                (jsonNode.at("/i18n/en") as ObjectNode).put(message, "")
            }
            json = mapper.toPrettyStringNotNull(jsonNode)
        } catch (ex: Exception) {
            CiteckMessages.error("Error while insert component", "Incorrect file content", editor.project)
            return
        }
        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().runUndoTransparentAction { editor.document.setText(json) }
        }
    }

    override fun update(presentation: Presentation, project: Project, editor: Editor, psiFile: PsiFile) {
        presentation.isVisible = ArtifactTypes.isForm(psiFile)
    }
}
