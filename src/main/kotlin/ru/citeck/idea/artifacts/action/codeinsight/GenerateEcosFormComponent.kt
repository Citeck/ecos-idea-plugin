package ru.citeck.idea.artifacts.action.codeinsight

import com.intellij.codeInsight.actions.SimpleCodeInsightAction
import com.intellij.ide.actions.QualifiedNameProviderUtil
import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonProperty
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import ecos.com.fasterxml.jackson210.databind.JsonNode
import ecos.com.fasterxml.jackson210.databind.ObjectMapper
import ecos.com.fasterxml.jackson210.databind.node.ArrayNode
import ru.citeck.ecos.commons.json.Json.mapper
import ru.citeck.idea.artifacts.action.codeinsight.forms.components.*
import ru.citeck.idea.artifacts.ArtifactTypes
import ru.citeck.idea.json.PsiJPath
import ru.citeck.idea.search.index.IndexKey
import ru.citeck.idea.search.index.IndexValue
import ru.citeck.idea.search.index.IndexesService
import ru.citeck.idea.utils.CiteckMessages
import java.util.*
import java.util.function.Supplier
import java.util.stream.Collectors

class GenerateEcosFormComponent : SimpleCodeInsightAction() {

    companion object {
        const val COMPONENTS_PATH: String = "/components"

        private val COMPONENTS: Map<String, Supplier<Component>> = mapOf(
            SelectJournal.TYPE to Supplier { SelectJournal() },
            DateTime.TYPE to Supplier { DateTime() },
            CheckBox.TYPE to Supplier { CheckBox() },
            TextField.TYPE to Supplier { TextField() },
            TextArea.TYPE to Supplier { TextArea() },
            Columns.TYPE + " (1 column)" to Supplier {
                Columns(
                    1
                )
            },
            Columns.TYPE + " (2 columns)" to Supplier {
                Columns(
                    2
                )
            },
            Panel.TYPE to Supplier { Panel() },
            EcosSelect.TYPE to Supplier { EcosSelect() }
        )
    }

    override fun invoke(project: Project, editor: Editor, psiFile: PsiFile) {
        val insertionPosition = getInsertionPath(editor, psiFile) ?: return

        JBPopupFactory
            .getInstance()
            .createPopupChooserBuilder(listOf<Any>(*COMPONENTS.keys.toTypedArray()))
            .setTitle("Select Component Type:")
            .setNamerForFiltering { obj: Any -> obj.toString() }
            .setRequestFocus(true)
            .setItemChosenCallback { componentType: Any? ->
                onComponentTypeSelected(
                    editor,
                    psiFile,
                    componentType as String?,
                    insertionPosition
                )
            }
            .createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)
    }

    private fun getInsertionPath(editor: Editor, psiFile: PsiFile): InsertionPosition? {
        val selectionStart = editor.selectionModel.selectionStart

        val selectedElement = psiFile.findElementAt(selectionStart) ?: return null
        val selectedProperty: PsiElement? = PsiTreeUtil.getParentOfType(
            selectedElement,
            JsonProperty::class.java
        )
        if (selectedProperty == null) {
            return null
        }

        val qualifiedName: String = DumbService.getInstance(psiFile.project)
            .computeWithAlternativeResolveEnabled(
                ThrowableComputable<String, RuntimeException> {
                    QualifiedNameProviderUtil.getQualifiedName(selectedProperty)
                }
            )

        var path = "/" + qualifiedName
            .replace("]", "/")
            .replace("[", "/")
            .replace(".", "/")
            .replace("//", "/")

        if (!path.contains(COMPONENTS_PATH)) {
            return null
        }

        path = path.substring(0, path.lastIndexOf(COMPONENTS_PATH) + COMPONENTS_PATH.length)

        var index = 0

        if (selectedProperty.children.size == 2 && selectedProperty.children[1] is JsonArray) {
            index = Arrays.stream(selectedProperty.children[1].children)
                .map { obj: PsiElement -> obj.textOffset }
                .filter { offset: Int -> offset < selectionStart }
                .count().toInt()
        }

        return InsertionPosition(path, index)
    }

    private fun onComponentTypeSelected(
        editor: Editor,
        psiFile: PsiFile,
        componentType: String?,
        insertionPosition: InsertionPosition
    ) {
        val component = COMPONENTS[componentType]!!
            .get()

        if (component is InputComponent) {
            JBPopupFactory
                .getInstance()
                .createPopupChooserBuilder(getAttributes(psiFile, editor.project!!, component))
                .setTitle("Select Attribute:")
                .setNamerForFiltering { obj: String -> obj }
                .setRequestFocus(true)
                .setItemChosenCallback { attribute: String ->
                    onAttributeSelected(
                        editor,
                        component,
                        attribute,
                        insertionPosition
                    )
                }
                .createPopup()
                .showInCenterOf(WindowManager.getInstance().getFrame(editor.project)!!.rootPane)
        } else {
            onAttributeSelected(editor, component, "", insertionPosition)
        }
    }

    private fun onAttributeSelected(
        editor: Editor,
        component: Component,
        attribute: String,
        insertionPosition: InsertionPosition
    ) {
        val project = editor.project

        val json: String

        try {
            val objectMapper = ObjectMapper()
            val jsonNode = objectMapper.readValue(
                editor.document.text,
                JsonNode::class.java
            )

            if (component is InputComponent) {
                component.setKey(attribute.replace(":", "_"))
                component.properties.attribute = attribute
                component.setLabel(attribute)
            } else {
                val keys = jsonNode.findValues("key")
                    .stream()
                    .map { obj: JsonNode -> obj.asText() }
                    .collect(Collectors.toSet())
                var key: String
                var i = 0
                do {
                    key = component.getType() + (++i)
                } while (keys.contains(key))
                component.setKey(key)
                component.setLabel(key)
            }

            (jsonNode.at(insertionPosition.path) as ArrayNode).insert(
                insertionPosition.index,
                objectMapper.convertValue(component, JsonNode::class.java)
            )

            json = mapper.toPrettyStringNotNull(jsonNode)
        } catch (ex: Exception) {
            CiteckMessages.error("Error while insert component", "Incorrect file content", project)
            return
        }

        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().runUndoTransparentAction {
                editor.document.setText(json)
            }
        }
    }

    private fun getAttributes(psiFile: PsiFile, project: Project, component: InputComponent): List<String> {

        val indexesService = IndexesService.getInstance(project)

        val typeRef = PsiJPath.parse("$.typeRef").getStr(psiFile) ?: return emptyList()
        val typeElement = indexesService.stream(IndexKey(typeRef)).findFirst().orElse(null)

        val typeRefFile = typeElement.getPsiElement(project) ?: return emptyList()
        val typeData = mapper.readData(typeRefFile.text)

        val typeRefAtts = typeData["$.model.attributes[:].id"].asStrList()

        if (typeRefAtts.isNotEmpty()) {
            return typeRefAtts
        }

        return component
            .getSupportedArtifactTypes()
            .stream()
            .map { type: String? -> IndexKey(type!!) }
            .flatMap { key: IndexKey? -> indexesService.stream(key!!) }
            .map(IndexValue::id)
            .collect(Collectors.toCollection { ArrayList() })
    }

    override fun update(presentation: Presentation, project: Project, editor: Editor, psiFile: PsiFile) {
        presentation.isVisible = ArtifactTypes.isForm(psiFile) && getInsertionPath(editor, psiFile) != null
    }

    private data class InsertionPosition(val path: String, val index: Int)
}
