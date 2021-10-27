package ru.citeck.actions

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.intellij.json.JsonElementTypes
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import com.intellij.testFramework.LightVirtualFile
import org.apache.commons.lang.StringEscapeUtils
import java.util.*
import javax.swing.*


class EditJsonStringAsJS : AnAction() {

    private val languageJson = Language.findLanguageByID("JSON")


    private class JsEditDialog(project: Project, text: String) : DialogWrapper(true) {

        private val fileEditor: FileEditor

        init {
            title = "Edit JavaScript"
            val lvf = LightVirtualFile(
                "${UUID.randomUUID()}.js",
                FileTypeManager.getInstance().getFileTypeByExtension("js"),
                text
            )
            fileEditor = TextEditorProvider.getInstance().createEditor(project, lvf)
            init()
        }

        val value: String get() {
            return (fileEditor as PsiAwareTextEditorImpl).editor.document.text
        }

        override fun createCenterPanel(): JComponent {
            return fileEditor.component
        }

    }


    override fun actionPerformed(e: AnActionEvent) {

        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val element = getDoubleQuotedString(e) ?: return
        val project = e.project ?: return
        val document = editor.document

        var text = StringEscapeUtils.unescapeJavaScript(element.text)
        text = text.substring(1, text.length - 1)

        val jsEditDialog = JsEditDialog(project, text)
        if (!jsEditDialog.showAndGet()) return

        WriteCommandAction.runWriteCommandAction(project) {
            document.replaceString(
                element.startOffset,
                element.endOffset,
                ObjectMapper().writeValueAsString(jsEditDialog.value).toString()
            )
        }

    }


    private fun getDoubleQuotedString(e: AnActionEvent): PsiElement? {
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return null
        if (psiFile.language != languageJson) return null

        val editor = e.getData(CommonDataKeys.EDITOR) ?: return null

        val primaryCaret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart

        val element = psiFile.findElementAt(start)
        if (element.elementType == JsonElementTypes.DOUBLE_QUOTED_STRING) return element
        return null
    }


    override fun update(e: AnActionEvent) {
        e.presentation.isVisible = getDoubleQuotedString(e) != null
    }


}